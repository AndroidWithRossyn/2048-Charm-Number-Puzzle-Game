package com.rossyn.blocktiles.game2048.presentation.activities;


import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;

import com.rossyn.blocktiles.game2048.R;
import com.rossyn.blocktiles.game2048.presentation.components.GameViewCell;
import com.rossyn.blocktiles.game2048.domain.models.ScoreModel;
import com.rossyn.blocktiles.game2048.data.prefs.ScoreBoardBuilder;
import com.rossyn.blocktiles.game2048.domain.utils.ThreadMain;
import com.rossyn.blocktiles.game2048.services.Music;
import com.rossyn.blocktiles.game2048.presentation.components.BitmapCreator;
import com.rossyn.blocktiles.game2048.presentation.adapter.ScoreAdapter;
import com.rossyn.blocktiles.game2048.presentation.interfaces.OnHomePressedListener;
import com.rossyn.blocktiles.game2048.data.prefs.SharedPref;


import java.util.ArrayList;
import java.util.Objects;


public class GameActivity extends BaseActivity {

    private static Context context;
    private TextView scoreTextView, topScoreTextView;
    private int boardRows, boardCols, boardExponent, gameMode;
    private boolean isTutorialFromMainScreen;


    GameViewCell gameViewCell;

    @Override
    protected void onBackPressedCustom() {
        destroyGameThread();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        boardRows = getIntent().getIntExtra("rows", 4);
        boardCols = getIntent().getIntExtra("cols", 4);
        boardExponent = getIntent().getIntExtra("exponent", 2);
        gameMode = getIntent().getIntExtra("game_mode", 0);
        isTutorialFromMainScreen = getIntent().getBooleanExtra("tutorial", false);
        setContentView(R.layout.game_activity);

        changeLayout();


        Animation btnScaleAnim = AnimationUtils.loadAnimation(GameActivity.this, R.anim.scale_animation);

        ImageButton homeBtn = findViewById(R.id.ib_home);
        homeBtn.setOnClickListener(v -> {
            startActivity(new Intent(GameActivity.this, HomeActivity.class));
            finish();
        });

        gameViewCell = findViewById(R.id.game_view_cell);
        gameViewCell.initSwipeListener(findViewById(R.id.relativeLayout));

        ImageButton btnSetting = findViewById(R.id.btn_settings);
        btnSetting.setOnClickListener(v -> {
            playClick();
            settingsDialog();
        });

        ImageButton btnScoreBoard = findViewById(R.id.btn_Score_board);
        btnScoreBoard.startAnimation(btnScaleAnim);
        btnScoreBoard.setOnClickListener(v -> {
            playClick();
            scoreBoardDialog();
        });


    }

    public static Context getContext() {
        return context;
    }

    public int getBoardRows() {
        return boardRows;
    }

    public int getBoardCols() {
        return boardCols;
    }

    public int getBoardExponent() {
        return boardExponent;
    }

    public int getGameMode() {
        return gameMode;
    }

    public boolean isTutorial() {
        if (sharedPref.getBoolean(SharedPref.TUTORIAL_PLAYED) || isTutorialFromMainScreen) {
            sharedPref.setBoolean(SharedPref.TUTORIAL_PLAYED, true);
            return true;
        } else {
            return false;
        }

    }

    public boolean isTutorialFromMainScreen() {
        return isTutorialFromMainScreen;
    }

    public boolean isSoundPlayed() {
        return sharedPref.getBoolean(SharedPref.MUTE_SOUNDS);
    }

    private void changeLayout() {

        LinearLayout layout = findViewById(R.id.game_liner_layout);
        ViewGroup.LayoutParams params = layout.getLayoutParams();
        params.width = params.height = Resources.getSystem().getDisplayMetrics().widthPixels;
        double difference = (double) boardCols / boardRows;

        if (boardRows == 3 && boardCols == 3) {
            params.width = params.height = (int) (params.width * 0.8);
        }
        if (boardRows == 4 && boardCols == 4) {
            params.width = params.height = (int) (params.width * 0.85);
        }
        if (boardRows != boardCols) {
            params.width = params.height = (int) (params.width * 1.1);
            params.width = (int) (params.width * difference);
        }
        layout.setLayoutParams(params);
    }

    public void updateScore(final long score, final long topScore) {
        this.scoreTextView = findViewById(R.id.current_score_textview);
        this.topScoreTextView = findViewById(R.id.best_score_textview);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (score == 0) {
                    scoreTextView.setText(getString(R.string.start));
                } else {
                    scoreTextView.setText(String.valueOf(score));
                }
                topScoreTextView.setText(String.valueOf(topScore));
            }
        });

    }

    private void settingsDialog() {

        final Dialog dialog = new Dialog(GameActivity.this);
        dialog.setContentView(R.layout.dialog_setting);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);
        dialog.show();


        final RadioGroup rgMusic = dialog.findViewById(R.id.radiogroup_music_select);
        final RadioButton musicOn = dialog.findViewById(R.id.music_on);
        final RadioButton musicOff = dialog.findViewById(R.id.music_off);

        if (sharedPref.getBoolean(SharedPref.MUTE_MUSIC)) {
            rgMusic.check(musicOn.getId());
            musicOn.setTextColor(Color.rgb(90, 85, 83));
        } else {
            rgMusic.check(musicOff.getId());
            musicOff.setTextColor(Color.rgb(90, 85, 83));
        }


        rgMusic.setOnCheckedChangeListener((group, checkedId) -> {
            playClick();
            if (checkedId == musicOn.getId()) {
                musicOn.setTextColor(Color.rgb(90, 85, 83));
                musicOff.setTextColor(Color.rgb(167, 168, 168));

                sharedPref.setBoolean(SharedPref.MUTE_SOUNDS, false);

            } else {
                musicOff.setTextColor(Color.rgb(90, 85, 83));
                musicOn.setTextColor(Color.rgb(167, 168, 168));

                sharedPref.setBoolean(SharedPref.MUTE_MUSIC, true);

            }
        });


        RadioGroup rgSound = dialog.findViewById(R.id.radiogroup_sound_select);
        final RadioButton soundOn = dialog.findViewById(R.id.sound_on);
        final RadioButton soundOff = dialog.findViewById(R.id.sound_off);

        if (sharedPref.getBoolean(SharedPref.MUTE_SOUNDS)) {
            rgSound.check(soundOn.getId());
            soundOn.setTextColor(Color.rgb(90, 85, 83));
        } else {
            rgSound.check(soundOff.getId());
            soundOff.setTextColor(Color.rgb(90, 85, 83));
        }


        rgSound.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == soundOn.getId()) {
                sharedPref.setBoolean(SharedPref.MUTE_SOUNDS, false);
                playClick();
                soundOn.setTextColor(Color.rgb(90, 85, 83));
                soundOff.setTextColor(Color.rgb(167, 168, 168));

            } else {
                sharedPref.setBoolean(SharedPref.MUTE_SOUNDS, true);
                soundOff.setTextColor(Color.rgb(90, 85, 83));
                soundOn.setTextColor(Color.rgb(167, 168, 168));
            }
        });

        Animation anim = AnimationUtils.loadAnimation(GameActivity.this, R.anim.scale_animation);
        Button closeBtn = dialog.findViewById(R.id.close_button);
        closeBtn.startAnimation(anim);
        closeBtn.setOnClickListener(v -> {
            playClick();
            dialog.dismiss();
//                hideSystemUI();

        });


    }

    private void scoreBoardDialog() {
        SharedPreferences sharedPreferences = getSharedPreferences(SharedPref.SHARED_PREF_FILE_NAME, MODE_PRIVATE);
        final Dialog dialog = new Dialog(GameActivity.this);
        dialog.setContentView(R.layout.dialog_scoreboard);

        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.90);
        Objects.requireNonNull(dialog.getWindow()).setLayout(width, height);


        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final ListView listView = dialog.findViewById(R.id.listview_score_board);
        ArrayList<ScoreModel> classicScoreModels = ScoreBoardBuilder.createClassicArrayList(sharedPreferences, "0");
        ArrayList<ScoreModel> blocksScoreModels = ScoreBoardBuilder.createClassicArrayList(sharedPreferences, "1");
        ArrayList<ScoreModel> shuffleScoreModels = ScoreBoardBuilder.createClassicArrayList(sharedPreferences, "2");
        final ScoreAdapter scoreAdapterClassic = new ScoreAdapter(classicScoreModels, this);
        final ScoreAdapter scoreAdapterBlocks = new ScoreAdapter(blocksScoreModels, this);
        final ScoreAdapter scoreAdapterShuffle = new ScoreAdapter(shuffleScoreModels, this);
        listView.setAdapter(scoreAdapterClassic);

        final Animation rightInAnim = AnimationUtils.loadAnimation(GameActivity.this, R.anim.slide_in_right_side);
        final Animation leftInAnim = AnimationUtils.loadAnimation(GameActivity.this, R.anim.slide_in_left_side);
        final Animation rightOutAnim = AnimationUtils.loadAnimation(GameActivity.this, R.anim.slide_out_right_side);
        final Animation leftOutAnim = AnimationUtils.loadAnimation(GameActivity.this, R.anim.slide_out_left_side);

        Animation anim = AnimationUtils.loadAnimation(GameActivity.this, R.anim.scale_animation);
        final TextView currentModeTv = dialog.findViewById(R.id.textview_mode_type);
        final int[] index = {0};

        ImageButton btnRight = dialog.findViewById(R.id.right_btn);
        btnRight.startAnimation(anim);
        btnRight.setOnClickListener(v -> {
            if (index[0] == 2) {
                index[0] = 0;
            } else {
                index[0]++;
            }
            leftOutAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    switch (index[0]) {
                        case 0:
                            playClick();
                            listView.setAdapter(scoreAdapterClassic);
                            currentModeTv.setText(getString(R.string.mode_classic));
                            break;
                        case 1:
                            playClick();
                            listView.setAdapter(scoreAdapterBlocks);
                            currentModeTv.setText(getString(R.string.mode_blocks));
                            break;
                        case 2:
                            playClick();
                            listView.setAdapter(scoreAdapterShuffle);
                            currentModeTv.setText(getString(R.string.mode_shuffle));
                            break;
                    }
                    listView.startAnimation(rightInAnim);
                }
            });
            listView.startAnimation(leftOutAnim);


        });

        ImageButton btnLeft = dialog.findViewById(R.id.left_btn);
        btnLeft.startAnimation(anim);
        btnLeft.setOnClickListener(v -> {
            if (index[0] == 0) {
                index[0] = 2;
            } else {
                index[0]--;
            }

            rightOutAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    switch (index[0]) {
                        case 0:
                            playClick();
                            listView.setAdapter(scoreAdapterClassic);
                            currentModeTv.setText(getString(R.string.mode_classic));
                            break;
                        case 1:
                            playClick();
                            listView.setAdapter(scoreAdapterBlocks);
                            currentModeTv.setText(getString(R.string.mode_blocks));
                            break;
                        case 2:
                            playClick();
                            listView.setAdapter(scoreAdapterShuffle);
                            currentModeTv.setText(getString(R.string.mode_shuffle));
                            break;
                    }
                    listView.startAnimation(leftInAnim);
                }
            });
            listView.startAnimation(rightOutAnim);


        });
        Animation btnScaleAnim = AnimationUtils.loadAnimation(GameActivity.this, R.anim.scale_animation);
        Button btnClose = dialog.findViewById(R.id.close_button);
        btnClose.setAnimation(btnScaleAnim);
        btnClose.setOnClickListener(v -> {
            playClick();
            dialog.dismiss();
//                hideSystemUI();

        });
        dialog.show();

    }

    private ThreadMain thread;

    public void setThread(ThreadMain thread) {
        this.thread = thread;
    }

    public void destroyGameThread() {
        boolean retry = true;
        while (retry) {
            try {
                thread.setRunning(false);
                thread.join();
                retry = false;
                BitmapCreator bitmapCreator = new BitmapCreator();
                bitmapCreator.clearBitmapArray();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }



}








