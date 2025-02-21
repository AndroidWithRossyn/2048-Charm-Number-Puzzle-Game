package com.rossyn.blocktiles.game2048.services;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import com.rossyn.blocktiles.game2048.R;

import timber.log.Timber;


public class Music extends Service implements MediaPlayer.OnErrorListener {

    private final IBinder mBinder = new ServiceBinder();
    private MediaPlayer mediaPlayer;
    private int pausePosition = 0;

    public class ServiceBinder extends Binder {
        public Music getService() {
            return Music.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initMediaPlayer();
    }


    private void initMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        mediaPlayer = MediaPlayer.create(this, R.raw.background_sound);
        if (mediaPlayer != null) {
            mediaPlayer.setLooping(true);
            mediaPlayer.setVolume(0.3f, 0.3f);
            mediaPlayer.setOnErrorListener(this);
        } else {
            Timber.tag("MusicService").e("MediaPlayer initialization failed.");
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
        return START_NOT_STICKY;
    }


    public void pauseMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            pausePosition = mediaPlayer.getCurrentPosition();
        }
    }


    public void resumeMusic() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(pausePosition);
            mediaPlayer.start();
        }
    }


    public void startMusic() {
        if (mediaPlayer == null) {
            initMediaPlayer();
        }
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }



    public void stopMusic() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopMusic();
    }


    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        stopMusic();
        return false;
    }
}
