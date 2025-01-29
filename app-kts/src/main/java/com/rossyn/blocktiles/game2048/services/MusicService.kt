package com.rossyn.blocktiles.game2048.services


import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import com.rossyn.blocktiles.game2048.R
import timber.log.Timber
import javax.inject.Singleton

@Singleton
class MusicService : Service(), MediaPlayer.OnErrorListener {

    private val binder = LocalBinder()
    private var mediaPlayer: MediaPlayer? = null
    private var mCurrentPosition = 0

    inner class LocalBinder : Binder() {
        fun getService(): MusicService = this@MusicService
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        startMusic()
        Timber.tag("MusicService").d("onCreate")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        mediaPlayer?.start()
        return START_NOT_STICKY
    }

    fun startMusic() {
        mediaPlayer = MediaPlayer.create(this, R.raw.background_sound).apply {
            setOnErrorListener(this@MusicService)
            isLooping = true
            setVolume(0.3f, 0.3f)
            start()
        }
    }

    fun pauseMusic() {
        mediaPlayer?.takeIf { it.isPlaying }?.apply {
            pause()
            mCurrentPosition = currentPosition
        }
    }

    fun resumeMusic() {
        mediaPlayer?.takeIf { !it.isPlaying }?.apply {
            seekTo(mCurrentPosition)
            start()
        }
    }

    private fun stopMusic() {
        mediaPlayer?.apply {
            stop()
            release()
        }
        mediaPlayer = null

        Timber.tag("MusicService").d("stopMusic")
    }

    override fun onDestroy() {
        stopMusic()
        super.onDestroy()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        stopSelf()
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        stopMusic()
        Timber.tag("MusicService").d("onError")
        return false
    }
}
