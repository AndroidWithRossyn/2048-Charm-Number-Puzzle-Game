package com.rossyn.blocktiles.game2048.domain.models

import android.graphics.drawable.Drawable
import com.rossyn.blocktiles.game2048.R

enum class Boards (val rows: Int, val cols: Int, val drawableResId: Int){
    // Square Boards
    BOARD_4x4(4, 4, R.drawable.board_4x4),
    BOARD_5x5(5, 5, R.drawable.board_5x5),
    BOARD_6x6(6, 6, R.drawable.board_6x6),
    BOARD_8x8(8, 8, R.drawable.board_8x8),
    BOARD_3x3(3, 3, R.drawable.board_3x3),

    // Rectangle Boards
    BOARD_4x3(4, 3, R.drawable.board_3x4),
    BOARD_5x3(5, 3, R.drawable.board_3x5),
    BOARD_5x4(5, 4, R.drawable.board_4x5),
    BOARD_6x5(6, 5, R.drawable.board_5x6);

    fun toBoardType(getDrawable: (Int) -> Drawable?): BoardType {
        val drawable = getDrawable(drawableResId)
        return BoardType(rows, cols, drawable)
    }

    companion object {
        fun getSquareBoards(getDrawable: (Int) -> Drawable?): List<BoardType> {
            return values()
                .filter { it.rows == it.cols }
                .map { it.toBoardType(getDrawable) }
        }
        fun getRectangleBoards(getDrawable: (Int) -> Drawable?): List<BoardType> {
            return values()
                .filter { it.rows != it.cols }
                .map { it.toBoardType(getDrawable) }
        }
    }
}