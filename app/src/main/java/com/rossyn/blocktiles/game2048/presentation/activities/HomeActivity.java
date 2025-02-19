

package com.rossyn.blocktiles.game2048.presentation.activities;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;

import com.rossyn.blocktiles.game2048.R;
import com.rossyn.blocktiles.game2048.data.prefs.ScoreBoardBuilder;
import com.rossyn.blocktiles.game2048.domain.models.ScoreModel;
import com.rossyn.blocktiles.game2048.presentation.adapter.ScoreAdapter;
import com.rossyn.blocktiles.game2048.data.prefs.SharedPref;

import java.util.ArrayList;
import java.util.Objects;

public class HomeActivity extends BaseActivity {

    @Override
    protected void onBackPressedCustom() {
        finish();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        Animation btnScaleAnim = AnimationUtils.loadAnimation(HomeActivity.this, R.anim.scale_animation);

        Button btnNewGame = findViewById(R.id.btn_new_game);
        btnNewGame.startAnimation(btnScaleAnim);
        btnNewGame.setOnClickListener(v -> {
            playClick();
            Intent intent = new Intent(HomeActivity.this, BoardOptionsActivity.class);
            startActivity(intent);
        });

        Button btnHowtoPlay = findViewById(R.id.btn_load_game);
        btnHowtoPlay.startAnimation(btnScaleAnim);
        btnHowtoPlay.setOnClickListener(v -> {
            playClick();
            Intent intent = new Intent(HomeActivity.this, GameActivity.class);
            intent.putExtra("tutorial", true);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right_side, R.anim.slide_out_left_side);
        });

        Button btnSettings = findViewById(R.id.btn_settings);
        btnSettings.startAnimation(btnScaleAnim);
        btnSettings.setOnClickListener(v -> {
            playClick();
            settingsDialog();
        });

        Button btnScoreBoard = findViewById(R.id.btn_Score_board);
        btnScoreBoard.startAnimation(btnScaleAnim);
        btnScoreBoard.setOnClickListener(v -> {
            playClick();
            scoreBoardDialog();
        });

        Button btnAbout = findViewById(R.id.btn_About);
        btnAbout.startAnimation(btnScaleAnim);
        btnAbout.setOnClickListener(v -> {
            playClick();
            Intent intent = new Intent(HomeActivity.this, InfoActivity.class);
            startActivity(intent);
        });

        Button btnRate = findViewById(R.id.btn_rate);
        btnRate.startAnimation(btnScaleAnim);
        btnRate.setOnClickListener(v -> {
            playClick();
            Toast.makeText(this, "PlayStore URL", Toast.LENGTH_SHORT).show();
        });

        Button btnmoregames = findViewById(R.id.btn_more_games);
        btnmoregames.startAnimation(btnScaleAnim);
        btnmoregames.setOnClickListener(v -> {
            playClick();
//            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.moregamesurl))));
        });

    }


    private void scoreBoardDialog() {
        SharedPreferences sharedPreferences = getSharedPreferences(SharedPref.SHARED_PREF_FILE_NAME, MODE_PRIVATE);
        final Dialog dialog = new Dialog(HomeActivity.this);
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

        final Animation rightInAnim = AnimationUtils.loadAnimation(HomeActivity.this, R.anim.slide_in_right_side);
        final Animation leftInAnim = AnimationUtils.loadAnimation(HomeActivity.this, R.anim.slide_in_left_side);
        final Animation rightOutAnim = AnimationUtils.loadAnimation(HomeActivity.this, R.anim.slide_out_right_side);
        final Animation leftOutAnim = AnimationUtils.loadAnimation(HomeActivity.this, R.anim.slide_out_left_side);

        Animation anim = AnimationUtils.loadAnimation(HomeActivity.this, R.anim.scale_animation);
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
        Animation btnScaleAnim = AnimationUtils.loadAnimation(HomeActivity.this, R.anim.scale_animation);
        Button btnClose = dialog.findViewById(R.id.close_button);
        btnClose.setAnimation(btnScaleAnim);
        btnClose.setOnClickListener(v -> {
            playClick();
            dialog.dismiss();
        });
        dialog.show();

    }

    private void settingsDialog() {


        final Dialog dialog = new Dialog(HomeActivity.this);
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

                sharedPref.setBoolean(SharedPref.MUTE_MUSIC, false);
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

        Animation btnScaleAnim = AnimationUtils.loadAnimation(HomeActivity.this, R.anim.scale_animation);
        Button closeBtn = dialog.findViewById(R.id.close_button);
        closeBtn.startAnimation(btnScaleAnim);
        closeBtn.setOnClickListener(v -> {
            playClick();
            dialog.dismiss();

        });

    }



}
