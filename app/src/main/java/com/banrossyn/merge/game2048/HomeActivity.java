package com.banrossyn.merge.game2048;


import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
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

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.banrossyn.merge.game2048.GameCode.ScoreModel;
import com.banrossyn.merge.game2048.GameCode.ScoreBoardBuilder;
import com.banrossyn.merge.game2048.Service.HomeWatcher;
import com.banrossyn.merge.game2048.Service.Music;
import com.banrossyn.merge.game2048.adapter.ScoreAdapter;
import com.banrossyn.merge.game2048.interfaces.OnHomePressedListener;
import com.banrossyn.merge.game2048.util.Utils;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.Task;
import com.google.android.ump.ConsentForm;
import com.google.android.ump.ConsentInformation;
import com.google.android.ump.ConsentRequestParameters;
import com.google.android.ump.UserMessagingPlatform;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class HomeActivity extends AppCompatActivity {

    HomeWatcher mHomeWatcher;
    private ReviewInfo reviewInfo;
    private ReviewManager manager;
    AppUpdateManager appUpdateManager;
    Context update;
    private Utils utils;
    private ConsentInformation consentInformation;
    private final AtomicBoolean isMobileAdsInitializeCalled = new AtomicBoolean(false);


    public void GDPRMessage() {
//        ConsentDebugSettings debugSettings = new ConsentDebugSettings.Builder(this)
//                .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
//                .addTestDeviceHashedId("4F45E161A3144238FA2A0869C6BB2EE1")
//                .build();
        // Set tag for under age of consent. false means users are not under age
        // of consent.
        ConsentRequestParameters params = new ConsentRequestParameters
                .Builder()
//                .setConsentDebugSettings(debugSettings)
                .setTagForUnderAgeOfConsent(false)
                .build();

        consentInformation = UserMessagingPlatform.getConsentInformation(this);
        consentInformation.requestConsentInfoUpdate(
                this,
                params,
                (ConsentInformation.OnConsentInfoUpdateSuccessListener) () -> {
                    UserMessagingPlatform.loadAndShowConsentFormIfRequired(
                            this,
                            (ConsentForm.OnConsentFormDismissedListener) loadAndShowError -> {
                                if (loadAndShowError != null) {
                                    // Consent gathering failed.
                                    Log.w("TAG", String.format("%s: %s",
                                            loadAndShowError.getErrorCode(),
                                            loadAndShowError.getMessage()));
                                }

                                // Consent has been gathered.
                                if (consentInformation.canRequestAds()) {
                                    initializeMobileAdsSdk();
                                }
                            }
                    );
                },
                (ConsentInformation.OnConsentInfoUpdateFailureListener) requestConsentError -> {
                    // Consent gathering failed.
                    Log.w("TAG", String.format("%s: %s",
                            requestConsentError.getErrorCode(),
                            requestConsentError.getMessage()));
                });

        // Check if you can initialize the Google Mobile Ads SDK in parallel
        // while checking for new consent information. Consent obtained in
        // the previous session can be used to request ads.
        if (consentInformation.canRequestAds()) {
            initializeMobileAdsSdk();
        }
    }


    private void initializeMobileAdsSdk() {
        if (isMobileAdsInitializeCalled.getAndSet(true)) {
            return;
        }
        // Initialize the Google Mobile Ads SDK.
        MobileAds.initialize(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        utils = new Utils(HomeActivity.this);
        if (!utils.getBooleanValue(Utils.mute_music)) {
            playMusic();
        }

        update = HomeActivity.this;
        hideSystemUI();
        activateReviewInfo();
        GDPRMessage();

        appUpdateManager = AppUpdateManagerFactory.create(update);
        UpdateApp();
        Animation btnScaleAnim = AnimationUtils.loadAnimation(HomeActivity.this, R.anim.scale_animation);

        Button btnNewGame = findViewById(R.id.btn_new_game);
        btnNewGame.startAnimation(btnScaleAnim);
        btnNewGame.setOnClickListener(v -> {
            playClick();
            Intent intent = new Intent(HomeActivity.this, ChooseBoardActivity.class);
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
            Intent intent = new Intent(HomeActivity.this, AboutActivity.class);
            startActivity(intent);
        });

        Button btnRate = findViewById(R.id.rate);
        btnRate.startAnimation(btnScaleAnim);
        btnRate.setOnClickListener(v -> {
            playClick();
            startReviewFlow();
        });

        Button btnmoregames = findViewById(R.id.btn_moregames);
        btnmoregames.startAnimation(btnScaleAnim);
        btnmoregames.setOnClickListener(v -> {
            playClick();
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.moregamesurl))));
        });



    }


    private void scoreBoardDialog() {
        SharedPreferences sharedPreferences = getSharedPreferences(Utils.SHAREDPREFERENCEFILENAME, MODE_PRIVATE);
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
        Animation btnScaleAnim = AnimationUtils.loadAnimation(HomeActivity.this, R.anim.scale_animation);
        Button btnClose = dialog.findViewById(R.id.close_button);
        btnClose.setAnimation(btnScaleAnim);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playClick();
                dialog.dismiss();
                hideSystemUI();
            }
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

        Animation btnScaleAnim = AnimationUtils.loadAnimation(HomeActivity.this, R.anim.scale_animation);
        Button closeBtn = dialog.findViewById(R.id.close_button);
        closeBtn.startAnimation(btnScaleAnim);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playClick();
                dialog.dismiss();
                hideSystemUI();

            }
        });

    }

    private void playClick() {
        final MediaPlayer click = MediaPlayer.create(HomeActivity.this, R.raw.click);
        if(!utils.getBooleanValue(Utils.mute_sounds)){
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

    private boolean mIsBound = false;
    private Music mServ;
    private ServiceConnection Scon = new ServiceConnection() {

        public void onServiceConnected(ComponentName name, IBinder binder) {
            mServ = ((Music.ServiceBinder) binder).getService();
        }

        public void onServiceDisconnected(ComponentName name) {
            mServ = null;
        }
    };

    void doBindService() {
        bindService(new Intent(this, Music.class), Scon, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void doUnbindService() {
        if (mIsBound) {
            unbindService(Scon);
            mIsBound = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mServ != null) {
            mServ.resumeMusic();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    private void hideSystemUI() {

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        View mContentView = getWindow().getDecorView();

        if (Build.VERSION.SDK_INT >= 30) {
            mContentView.getWindowInsetsController().hide(
                    android.view.WindowInsets.Type.statusBars() | android.view.WindowInsets.Type.navigationBars());
        } else {

            mContentView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LOW_PROFILE
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            );
        }


    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        hideSystemUI();
        super.onWindowFocusChanged(hasFocus);
    }

    void activateReviewInfo() {
        manager = ReviewManagerFactory.create(this);
        Task<ReviewInfo> managerInfoTask = manager.requestReviewFlow();
        managerInfoTask.addOnCompleteListener((task) -> {
            if (task.isSuccessful()) {
                reviewInfo = task.getResult();

            } else {

                Toast.makeText(this, "Review failed to start", Toast.LENGTH_SHORT).show();
            }
        });


    }

    void startReviewFlow() {
        if (reviewInfo != null) {
            Task<Void> flow = manager.launchReviewFlow(this, reviewInfo);
            flow.addOnCompleteListener(task ->
            {
                HomeActivity.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
            });
        }
    }

    public void UpdateApp() {
        try {
            Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
            appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                        && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                    try {
                        appUpdateManager.startUpdateFlowForResult(
                                appUpdateInfo, AppUpdateType.FLEXIBLE, HomeActivity.this, 101);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                }
            }).addOnFailureListener(e -> {
                e.printStackTrace();

            });

        } catch (Exception e) {
            e.printStackTrace();
        }

        appUpdateManager.registerListener(listener);
    }

    InstallStateUpdatedListener listener = installState -> {
        if (installState.installStatus() == InstallStatus.DOWNLOADED) {
            popUp();
        }

    };

    private void popUp() {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "App Update Almost Done", Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("Reload", v -> appUpdateManager.completeUpdate());
        snackbar.setTextColor(Color.parseColor("#FF0000"));
        snackbar.show();
    }


}
