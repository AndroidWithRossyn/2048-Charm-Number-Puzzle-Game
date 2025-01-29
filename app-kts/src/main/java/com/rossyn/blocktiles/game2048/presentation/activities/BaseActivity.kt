package com.rossyn.blocktiles.game2048.presentation.activities

import android.app.ActivityManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.SoundPool
import android.os.Bundle
import android.os.IBinder
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.DefaultLifecycleObserver
import com.rossyn.blocktiles.game2048.R
import com.rossyn.blocktiles.game2048.data.prefs.SharedPref
import com.rossyn.blocktiles.game2048.services.MusicService
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
abstract class BaseActivity : AppCompatActivity(), DefaultLifecycleObserver {

    private var mIsBound = false

    private var mServ: MusicService? = null

    @Inject
    lateinit var sharedPref: SharedPref

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            mServ = (binder as MusicService.LocalBinder).getService()
            mIsBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            mServ = null
            mIsBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super<AppCompatActivity>.onCreate(savedInstanceState)
        lifecycle.addObserver(this)

        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())


        Timber.tag("BaseMonitor").d("onCreate: ")

        soundPool = SoundPool.Builder()
            .setMaxStreams(5)
            .build()
        clickSoundId = soundPool.load(this, R.raw.click, 1)

        bindMusicService()
    }

    /**
     * Called when the activity becomes visible. Registers a callback to handle back press events.
     * If enabled, the back press is intercepted, disabled, and the custom method {@link #onBackPressedCustom()} is called.
     */
    override fun onStart() {
        super<AppCompatActivity>.onStart()

        Timber.tag("BaseMonitor").d("onStart: ")
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            /**
             * for new Api Level
             * Callback for handling the onBackPressed event.
             */
            override fun handleOnBackPressed() {
                if (isEnabled) {
                    isEnabled = true
                    onBackPressedCustom()
                }
            }
        })

    }

    /**
     * Override this method to provide custom back press behavior.
     */
    abstract fun onBackPressedCustom()


    private fun bindMusicService() {
        if (isAppInForeground()) {
            Intent(this, MusicService::class.java).also {
                startService(it)
                bindService(it, serviceConnection, Context.BIND_AUTO_CREATE)
            }
        } else {
            Timber.tag("ServiceError")
                .e("Cannot start service as the app is not in the foreground.")
        }
    }

    private lateinit var soundPool: SoundPool
    private var clickSoundId: Int = 0

    fun playClick() {
        if (!sharedPref.isMuteMusic) {
            soundPool.play(clickSoundId, 1f, 1f, 0, 0, 1f)
        }
    }


    /**
     * Called when the activity will start interacting with the user.
     * Resets the inactivity timer.
     */
    override fun onResume() {
        super<AppCompatActivity>.onResume()
        mServ?.resumeMusic()
        Timber.tag("BaseMonitor").d("onResume: ")
    }


    /**
     * Called when the system is about to start resuming another activity.
     * Removes any pending inactivity runnable.
     */
    override fun onPause() {
        mServ?.pauseMusic()
        Timber.tag("BaseMonitor").d("onPause: ")
        super<AppCompatActivity>.onPause()
    }


    /**
     * On destroy
     * Remove Lifecycle observer
     *
     */
    override fun onDestroy() {
        lifecycle.removeObserver(this)
        unbindMusicService()
        soundPool.release()
        Timber.tag("BaseMonitor").d("onDestroy")
        super<AppCompatActivity>.onDestroy()
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        mServ?.pauseMusic()
    }


    private fun unbindMusicService() {
        if (mIsBound) {
            unbindService(serviceConnection)
            mIsBound = false
        }
    }


    private fun isAppInForeground(): Boolean {
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val appProcesses = activityManager.runningAppProcesses ?: return false

        val packageName = packageName
        for (appProcess in appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                && appProcess.processName == packageName
            ) {
                return true
            }
        }
        return false
    }

}