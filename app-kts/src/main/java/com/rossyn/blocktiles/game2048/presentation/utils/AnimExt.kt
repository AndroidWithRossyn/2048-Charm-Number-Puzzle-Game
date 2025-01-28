package com.rossyn.blocktiles.game2048.presentation.utils

import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.rossyn.blocktiles.game2048.R

fun View.scaleAnim() {
    val animation: Animation = AnimationUtils.loadAnimation(context, R.anim.scale_animation)
    startAnimation(animation)
}

