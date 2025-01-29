package com.rossyn.blocktiles.game2048.presentation.utils

import android.content.Context
import android.view.View
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils
import androidx.annotation.AnimRes
import com.rossyn.blocktiles.game2048.R

fun View.scaleAnim() {
    val animation: Animation = AnimationUtils.loadAnimation(context, R.anim.scale_animation)
    startAnimation(animation)
}

fun Context.animLoad(@AnimRes res: Int): Animation {
    return AnimationUtils.loadAnimation(this, res)
}

fun Animation.onEnd(callback: () -> Unit) {
    setAnimationListener(object : AnimationListener {
        override fun onAnimationStart(animation: Animation?) {
        }

        override fun onAnimationEnd(animation: Animation?) {
            callback.invoke()
        }

        override fun onAnimationRepeat(animation: Animation?) {
        }

    })
}