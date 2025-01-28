package com.rossyn.blocktiles.game2048.presentation.utils

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.view.WindowManager
import android.view.WindowMetrics
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

fun AppCompatActivity.enableEdgeToEdgeAppTrans() {
    this.enableEdgeToEdge(
        statusBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT),
        navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
    )
}


fun Activity.keepScreenOn() {
    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
}

fun Activity.keepScreenOFF() {
    window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
}

fun Activity.getScreenHeight(): Int {
    val displayMetrics = resources.displayMetrics
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val windowMetrics: WindowMetrics = windowManager.currentWindowMetrics
        windowMetrics.bounds.height()
    } else {
        displayMetrics.heightPixels
    }

}