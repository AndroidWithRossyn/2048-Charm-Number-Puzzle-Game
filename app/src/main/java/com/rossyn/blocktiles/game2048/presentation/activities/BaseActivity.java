package com.rossyn.blocktiles.game2048.presentation.activities;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowMetrics;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.SystemBarStyle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import com.rossyn.blocktiles.game2048.R;
import com.rossyn.blocktiles.game2048.data.prefs.SharedPref;
import com.rossyn.blocktiles.game2048.services.Music;

import java.util.List;

import timber.log.Timber;

public abstract class BaseActivity extends AppCompatActivity implements DefaultLifecycleObserver {

    private SoundPool soundPool;
    private int clickSoundId = 0;

    public SharedPref sharedPref;
    Music musicService;
    boolean isBound = false;

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Music.ServiceBinder binder = (Music.ServiceBinder) service;
            musicService = binder.getService();
            isBound = true;
            musicService.startMusic();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
            musicService = null;
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        EdgeToEdge.enable(this, SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT), SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT));
        super.onCreate(savedInstanceState);
        getLifecycle().addObserver(this);

        Window window = getWindow();
        WindowInsetsControllerCompat windowInsetsController = WindowCompat.getInsetsController(window, window.getDecorView());
        windowInsetsController.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());


        sharedPref = new SharedPref(this);
        soundPool = new SoundPool.Builder().setMaxStreams(5).build();
        clickSoundId = soundPool.load(this, R.raw.click, 1);

        Timber.tag("BaseMonitor").d("onCreate: ");

        bindMusicService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getLifecycle().removeObserver(this);
        if (isBound /*&& musicService != null*/) {
            unbindService(serviceConnection);
            isBound = false;
        }
    }


    private void bindMusicService() {
        if (isAppInForeground()) {
            Intent intent = new Intent(this, Music.class);
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

        } else {
            Timber.tag("ServiceError").e("Cannot start service as the app is not in the foreground.");
        }
    }

    /**
     * Called when the activity becomes visible. Registers a callback to handle back press events.
     * If enabled, the back press is intercepted, disabled, and the custom method {@link #onBackPressedCustom()} is called.
     */
    @Override
    protected void onStart() {
        super.onStart();

        Timber.tag("BaseMonitor").d("onStart: ");
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            /**
             * for new Api Level
             * Callback for handling the onBackPressed event.
             */
            @Override
            public void handleOnBackPressed() {
                if (isEnabled()) {
                    setEnabled(true);
                    onBackPressedCustom();
                }
            }
        });
    }

    /**
     * Override this method to provide custom back press behavior.
     */
    protected abstract void onBackPressedCustom();

    public void playClick() {
        if (!sharedPref.getBoolean(SharedPref.MUTE_MUSIC)) {
            soundPool.play(clickSoundId, 1f, 1f, 0, 0, 1f);
        }
    }


    @Override
    public void onResume(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onResume(owner);
        if (musicService != null) musicService.resumeMusic();
        Timber.tag("BaseMonitor").d("onResume: ");
    }

    @Override
    public void onPause(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onPause(owner);
        if (musicService != null) musicService.pauseMusic();
        Timber.tag("BaseMonitor").d("onPause: ");
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        if (musicService != null) musicService.pauseMusic();
    }

    private boolean isAppInForeground() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager == null) {
            return false;
        }

        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }

        String packageName = getPackageName();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }


    public void keepScreenOn() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public void keepScreenOff() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public int getScreenHeight() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowMetrics windowMetrics = getWindowManager().getCurrentWindowMetrics();
            return windowMetrics.getBounds().height();
        } else {
            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            return displayMetrics.heightPixels;
        }
    }

    public int getScreenWidth() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowMetrics windowMetrics = getWindowManager().getCurrentWindowMetrics();
            return windowMetrics.getBounds().width();
        } else {
            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            return displayMetrics.widthPixels;
        }
    }


    public void navigateTo(Class<?> destination) {
        startActivity(new Intent(this, destination));
    }

}
