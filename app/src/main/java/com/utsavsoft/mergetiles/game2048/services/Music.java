package com.utsavsoft.mergetiles.game2048.services;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import com.utsavsoft.mergetiles.game2048.R;


public class Music extends Service implements MediaPlayer.OnErrorListener {

    private final IBinder mBinder = new ServiceBinder();

    MediaPlayer mediaPlayer;

    private int length = 0;

    public Music() {
    }

    public class ServiceBinder extends Binder {
        public Music getService() {
            return Music.this;
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mediaPlayer = MediaPlayer.create(this, R.raw.background_sound);
        mediaPlayer.setOnErrorListener(this);

        if (mediaPlayer != null) {
            mediaPlayer.setLooping(true);
            mediaPlayer.setVolume(0.3f, 0.3f);
        }


        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {

            public boolean onError(MediaPlayer mp, int what, int
                    extra) {

                onError(mediaPlayer, what, extra);
                return true;
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
        return START_NOT_STICKY;
    }

    public void pauseMusic() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                length = mediaPlayer.getCurrentPosition();
            }
        }
    }

    public void resumeMusic() {
        if (mediaPlayer != null) {
            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.seekTo(length);
                mediaPlayer.start();
            }
        }
    }

    public void startMusic() {
        mediaPlayer = MediaPlayer.create(this, R.raw.background_sound);
        mediaPlayer.setOnErrorListener(this);

        if (mediaPlayer != null) {
            mediaPlayer.setLooping(true);
            mediaPlayer.setVolume(0.2f, 0.2f);
            mediaPlayer.start();
        }

    }

    public void stopMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
                mediaPlayer.release();
            } finally {
                mediaPlayer = null;
            }
        }
    }

    public boolean onError(MediaPlayer mp, int what, int extra) {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
                mediaPlayer.release();
            } finally {
                mediaPlayer = null;
            }
        }
        return false;
    }


}