package com.banrossyn.merge.game2048;

import static com.banrossyn.merge.game2048.util.Utils.SHAREDPREFERENCEFILENAME;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
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

import androidx.appcompat.app.AppCompatActivity;

import com.banrossyn.merge.game2048.GameCode.ScoreModel;
import com.banrossyn.merge.game2048.GameCode.ScoreBoardBuilder;
import com.banrossyn.merge.game2048.GameCode.ThreadMain;
import com.banrossyn.merge.game2048.Service.HomeWatcher;
import com.banrossyn.merge.game2048.Service.Music;
import com.banrossyn.merge.game2048.Tiles.BitmapCreator;
import com.banrossyn.merge.game2048.adapter.ScoreAdapter;
import com.banrossyn.merge.game2048.interfaces.OnHomePressedListener;
import com.banrossyn.merge.game2048.util.Utils;


import java.util.ArrayList;
import java.util.Objects;


public class GameActivity extends AppCompatActivity {

    private static Context context;
    private TextView scoreTextView, topScoreTextView;
    HomeWatcher mHomeWatcher;
    private int boardRows, boardCols, boardExponent, gameMode;
    private boolean isTutorialFromMainScreen;

    private Utils utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        boardRows = getIntent().getIntExtra("rows", 4);
        boardCols = getIntent().getIntExtra("cols", 4);
        boardExponent = getIntent().getIntExtra("exponent", 2);
        gameMode = getIntent().getIntExtra("game_mode", 0);
        isTutorialFromMainScreen = getIntent().getBooleanExtra("tutorial", false);
        utils = new Utils(GameActivity.this);
        setContentView(R.layout.game_activity);

        if (!utils.getBooleanValue(Utils.mute_music)) {
            playMusic();
        }

        changeLayout();


        Animation btnScaleAnim = AnimationUtils.loadAnimation(GameActivity.this, R.anim.scale_animation);

        ImageButton homeBtn = findViewById(R.id.ib_home);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GameActivity.this, HomeActivity.class));
                finish();
            }
        });

        ImageButton btnSetting = findViewById(R.id.btn_settings);
        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playClick();
                settingsDialog();
            }
        });

        ImageButton btnScoreBoard = findViewById(R.id.btn_Score_board);
        btnScoreBoard.startAnimation(btnScaleAnim);
        btnScoreBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playClick();
                scoreBoardDialog();
            }
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
        if (!utils.getBooleanValue(Utils.tutorial_played) || isTutorialFromMainScreen) {
            utils.setBooleanValue(Utils.tutorial_played, true);
            return true;
        } else {
            return false;
        }

    }

    public boolean isTutorialFromMainScreen() {
        return isTutorialFromMainScreen;
    }

    public boolean isSoundPlayed() {
        return !utils.getBooleanValue(Utils.mute_sounds);
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

        if (!utils.getBooleanValue(Utils.mute_music)) {
            rgMusic.check(musicOn.getId());
            musicOn.setTextColor(Color.rgb(90, 85, 83));
        } else {
            rgMusic.check(musicOff.getId());
            musicOff.setTextColor(Color.rgb(90, 85, 83));
        }


        rgMusic.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                playClick();
                if (checkedId == musicOn.getId()) {
                    musicOn.setTextColor(Color.rgb(90, 85, 83));
                    musicOff.setTextColor(Color.rgb(167, 168, 168));
                    if (mServ != null) {
                        mServ.startMusic();
                    } else {
                        playMusic();
                    }
                    utils.setBooleanValue(Utils.mute_music, false);

                } else {
                    musicOff.setTextColor(Color.rgb(90, 85, 83));
                    musicOn.setTextColor(Color.rgb(167, 168, 168));
                    if (mServ != null) {
                        mServ.stopMusic();
                    }
                    utils.setBooleanValue(Utils.mute_music, true);

                }
            }
        });


        RadioGroup rgSound = dialog.findViewById(R.id.radiogroup_sound_select);
        final RadioButton soundOn = dialog.findViewById(R.id.sound_on);
        final RadioButton soundOff = dialog.findViewById(R.id.sound_off);

        if (!utils.getBooleanValue(Utils.mute_sounds)) {
            rgSound.check(soundOn.getId());
            soundOn.setTextColor(Color.rgb(90, 85, 83));
        } else {
            rgSound.check(soundOff.getId());
            soundOff.setTextColor(Color.rgb(90, 85, 83));
        }


        rgSound.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == soundOn.getId()) {
                    utils.setBooleanValue(Utils.mute_sounds, false);
                    playClick();
                    soundOn.setTextColor(Color.rgb(90, 85, 83));
                    soundOff.setTextColor(Color.rgb(167, 168, 168));

                } else {
                    utils.setBooleanValue(Utils.mute_sounds, true);
                    soundOff.setTextColor(Color.rgb(90, 85, 83));
                    soundOn.setTextColor(Color.rgb(167, 168, 168));
                }
            }
        });

        Animation anim = AnimationUtils.loadAnimation(GameActivity.this, R.anim.scale_animation);
        Button closeBtn = dialog.findViewById(R.id.close_button);
        closeBtn.startAnimation(anim);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playClick();
                dialog.dismiss();
//                hideSystemUI();

            }
        });


    }

    private void scoreBoardDialog() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHAREDPREFERENCEFILENAME, MODE_PRIVATE);
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
        btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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


            }
        });

        ImageButton btnLeft = dialog.findViewById(R.id.left_btn);
        btnLeft.startAnimation(anim);
        btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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


            }
        });
        Animation btnScaleAnim = AnimationUtils.loadAnimation(GameActivity.this, R.anim.scale_animation);
        Button btnClose = dialog.findViewById(R.id.close_button);
        btnClose.setAnimation(btnScaleAnim);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playClick();
                dialog.dismiss();
//                hideSystemUI();

            }
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

    private boolean mIsBound = false;
    private Music mServ;
    private ServiceConnection Scon = new ServiceConnection() {

        public void onServiceConnected(ComponentName name, IBinder
                binder) {
            mServ = ((Music.ServiceBinder) binder).getService();
        }

        public void onServiceDisconnected(ComponentName name) {
            mServ = null;
        }
    };

    void doBindService() {
        bindService(new Intent(this, Music.class),
                Scon, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void doUnbindService() {
        if (mIsBound) {
            unbindService(Scon);
            mIsBound = false;
        }
    }

    private void playClick() {
        final MediaPlayer click = MediaPlayer.create(GameActivity.this, R.raw.click);
        if (isSoundPlayed()) {
            click.start();
        }
    }

    private void playMusic() {

        doBindService();
        Intent music = new Intent();
        music.setClass(this, Music.class);
        startService(music);

        mHomeWatcher = new HomeWatcher(this);
        mHomeWatcher.setOnHomePressedListener(new OnHomePressedListener() {
            @Override
            public void onHomePressed() {
                if (mServ != null) {
                    mServ.pauseMusic();
                }
            }

            @Override
            public void onHomeLongPressed() {
                if (mServ != null) {
                    mServ.pauseMusic();
                }
            }
        });
        mHomeWatcher.startWatch();

    }

    @Override
    public void onBackPressed() {
        destroyGameThread();
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
//        hideSystemUI();
        super.onResume();


        if (mServ != null) {
            mServ.resumeMusic();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        PowerManager pm = (PowerManager)
                getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = false;
        if (pm != null) {
            isScreenOn = pm.isInteractive();
        }

        if (!isScreenOn) {
            if (mServ != null) {
                mServ.pauseMusic();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        doUnbindService();
        Intent music = new Intent();
        music.setClass(this, Music.class);
        stopService(music);

    }


}








