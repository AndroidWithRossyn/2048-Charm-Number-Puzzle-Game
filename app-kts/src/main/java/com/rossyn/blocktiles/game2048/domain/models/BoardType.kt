package com.rossyn.blocktiles.game2048.domain.models

import android.graphics.drawable.Drawable

data class BoardType(
    val rows: Int, val cols: Int, val drawable: Drawable?
) {
    fun getTypeString(): String = "$rows x $cols"
}