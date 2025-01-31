package com.rossyn.blocktiles.game2048.presentation.interfaces

import android.view.SurfaceHolder

interface GameInterface {
    fun surfaceCreated(holder: SurfaceHolder)
    fun surfaceChanged(holder: SurfaceHolder)
    fun surfaceDestroyed()
}