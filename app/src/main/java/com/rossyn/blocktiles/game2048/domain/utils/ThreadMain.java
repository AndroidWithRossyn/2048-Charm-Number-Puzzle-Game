package com.rossyn.blocktiles.game2048.domain.utils;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

import com.rossyn.blocktiles.game2048.presentation.components.GameViewCell;

import timber.log.Timber;

public class ThreadMain extends Thread {

    private SurfaceHolder surfaceHolder;
    private final GameViewCell gameViewCell;
    private boolean running;

    public ThreadMain(SurfaceHolder surfaceHolder, GameViewCell gameViewCell) {
        super();
        this.surfaceHolder = surfaceHolder;
        this.gameViewCell = gameViewCell;
    }

    public void setRunning(boolean isRunning) {
        running = isRunning;
    }

    public void setSurfaceHolder(SurfaceHolder surfaceHolder) {
        this.surfaceHolder = surfaceHolder;
    }

    @Override
    public void run() {
        long targetTime = 1000 / 60; // 60 fps

        while (running) {
          long  startTime = System.nanoTime();
            Canvas canvas = null;

            try {
                canvas = surfaceHolder.lockCanvas();
                if (canvas != null) {
                    synchronized (surfaceHolder) {
                        gameViewCell.update();
                        gameViewCell.draw(canvas);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (canvas != null) {
                    try {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Timber.e(e);
                    }
                }
            }

            long elapsedTimeMs  = (System.nanoTime() - startTime) / 100000;
            long waitTimeMs = targetTime - elapsedTimeMs;

            if (waitTimeMs > 0) {
                try {
                    Thread.sleep(waitTimeMs);
                } catch (InterruptedException e) {
//                    Thread.currentThread().interrupt();
                    Timber.e(e);
                }
            }
        }

    }

}

