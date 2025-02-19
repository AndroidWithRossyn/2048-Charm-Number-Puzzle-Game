package com.rossyn.blocktiles.game2048.domain.utils;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

import com.rossyn.blocktiles.game2048.presentation.components.GameViewCell;

public class ThreadMain extends Thread {

    private SurfaceHolder surfaceHolder;
    private final GameViewCell gameViewCell;
    private boolean running;

    public ThreadMain(SurfaceHolder surfaceHolder, GameViewCell gameViewCell) {
        super();
        this.surfaceHolder = surfaceHolder;
        this.gameViewCell = gameViewCell;
    }

    public void setRunning (boolean isRunning) {
        running = isRunning;
    }

    public void setSurfaceHolder(SurfaceHolder surfaceHolder){
        this.surfaceHolder= surfaceHolder;
    }

    @Override
    public void run() {
        long startTime,timeMillis,waitTime;
        int targetFPS = 60;
        long targetTime = 1000/ targetFPS;

        while (running){
            startTime = System.nanoTime();
            Canvas canvas = null;

            try {
                canvas =  surfaceHolder.lockCanvas();
                synchronized (surfaceHolder) {
                    gameViewCell.update();
                    gameViewCell.draw(canvas);
                }
            } catch (Exception e){
                e.printStackTrace();
            } finally {
                if( canvas !=  null){
                    try {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
            timeMillis = (System.nanoTime() -startTime)/100000;
            waitTime =  targetTime - timeMillis;

            try {
                if (waitTime > 0) {
                    sleep(waitTime);

                }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

    }

