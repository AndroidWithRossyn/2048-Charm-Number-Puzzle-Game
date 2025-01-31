package com.rossyn.blocktiles.game2048.presentation.components

import android.graphics.Canvas
import android.view.SurfaceHolder

class ThreadMain(private var surfaceHolder: SurfaceHolder, private val gameViewCell: GameViewCell) : Thread() {

    private var running: Boolean = false

    fun setRunning(isRunning: Boolean) {
        running = isRunning
    }

    fun setSurfaceHolder(surfaceHolder: SurfaceHolder) {
        this.surfaceHolder = surfaceHolder
    }

    override fun run() {
        var startTime: Long
        var timeMillis: Long
        var waitTime: Long
        val targetFPS = 60
        val targetTime = 1000 / targetFPS

        while (running) {
            startTime = System.nanoTime()
            var canvas: Canvas? = null

            try {
                canvas = surfaceHolder.lockCanvas()
                synchronized(surfaceHolder) {
                    gameViewCell.update()
                    gameViewCell.draw(canvas)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                if (canvas != null) {
                    try {
                        surfaceHolder.unlockCanvasAndPost(canvas)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            timeMillis = (System.nanoTime() - startTime) / 1000000
            waitTime = targetTime - timeMillis

            try {
                if (waitTime > 0) {
                    sleep(waitTime)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
