package com.rossyn.blocktiles.game2048.presentation.components

import android.graphics.Bitmap
import android.graphics.Canvas
import com.rossyn.blocktiles.game2048.domain.models.Position

class Tile(
    var value: Long,
    position: Position,
    private val callback: GameBoardView
) {
    private val winningValue = callback.winningValue
    private var solidLives = 0
    private var currentPosition = position
    private var desPosition = position
    private var currentPositionX = currentPosition.positionX
    private var currentPositionY = currentPosition.positionY
    private var desPositionX = desPosition.positionX
    private var desPositionY = desPosition.positionY
    private var currentCellHeight = 0
    private var currentCellWidth = 0
    private val defaultCellHeight: Int
    private val defaultCellWidth: Int
    private var isMoving = false
    private var increased = false
    private var isSolid = false
    private var isSolidGone = false
    private val bitmapCreator = BitmapCreator()

    init {
        defaultCellHeight = bitmapCreator.getCellDefaultHeight()
        defaultCellWidth = bitmapCreator.getCellDefaultWidth()
        currentCellHeight = defaultCellHeight / 5
        currentCellWidth = defaultCellWidth / 5
    }

    constructor(value: Int, position: Position, callback: GameBoardView, solidLives: Int) : this(value.toLong(), position, callback) {
        this.isSolid = true
        this.solidLives = solidLives
    }

    fun getValue(): Long = value
    fun getPosition(): Position = currentPosition
    fun isSolid(): Boolean = isSolid

    fun decreaseLiveCount() {
        solidLives--
    }

    fun move(position: Position) {
        desPosition = position
        desPositionX = desPosition.positionX
        desPositionY = desPosition.positionY
        isMoving = true
    }

    fun notAlreadyIncreased(): Boolean = !increased

    fun setIncreased(state: Boolean) {
        increased = state
    }

    fun copyTile(): Tile = Tile(value, currentPosition, callback)

    fun increaseValue() {
        value *= callback.exponent
        currentCellHeight = (defaultCellHeight * 1.4).toInt()
        currentCellWidth = (defaultCellWidth * 1.4).toInt()
        increased = false
        if (value == winningValue) {
            callback.setPlayerWon()
        }
    }

    fun update() {
        if (isMoving) {
            updatePosition()
        }
        if (isSolid && solidLives == 1) {
            removeSolidBlock()
            return
        }
        updateSize()
    }

    private fun updatePosition() {
        val movingSpeed = 100
        if (currentPositionX < desPositionX) {
            if (currentPositionX + movingSpeed > desPositionX) {
                currentPosition = desPosition
                currentPositionX = currentPosition.positionX
            } else {
                currentPositionX += movingSpeed
            }
        } else if (currentPositionX > desPositionX) {
            if (currentPositionX - movingSpeed < desPositionX) {
                currentPosition = desPosition
                currentPositionX = currentPosition.positionX
            } else {
                currentPositionX -= movingSpeed
            }
        }
        if (currentPositionY < desPositionY) {
            if (currentPositionY + movingSpeed > desPositionY) {
                currentPosition = desPosition
                currentPositionY = currentPosition.positionY
            } else {
                currentPositionY += movingSpeed
            }
        } else if (currentPositionY > desPositionY) {
            if (currentPositionY - movingSpeed < desPositionY) {
                currentPosition = desPosition
                currentPositionY = currentPosition.positionY
            } else {
                currentPositionY -= movingSpeed
            }
        }
    }

    private fun updateSize() {
        val sizeSpeed = 20
        if (currentCellHeight < defaultCellHeight || currentCellWidth < defaultCellWidth) {
            if (currentCellHeight + sizeSpeed > defaultCellHeight || currentCellWidth + sizeSpeed > defaultCellWidth) {
                currentCellHeight = defaultCellHeight
                currentCellWidth = defaultCellWidth
            } else {
                currentCellHeight += sizeSpeed
                currentCellWidth += sizeSpeed
            }
        }
        if (currentCellHeight > defaultCellHeight || currentCellWidth > defaultCellWidth) {
            if (currentCellHeight - sizeSpeed < defaultCellHeight || currentCellWidth - sizeSpeed < defaultCellWidth) {
                currentCellHeight = defaultCellHeight
                currentCellWidth = defaultCellWidth
            } else {
                currentCellHeight -= sizeSpeed
                currentCellWidth -= sizeSpeed
            }
        }
    }

    fun draw(canvas: Canvas) {
        var bitmap = bitmapCreator.getBitmap(value)
        bitmap = Bitmap.createScaledBitmap(bitmap, currentCellWidth, currentCellHeight, false)
        canvas.drawBitmap(
            bitmap,
            (currentPositionX + (defaultCellWidth / callback.exponent - currentCellWidth / callback.exponent)).toFloat(),
            (currentPositionY + (defaultCellHeight / callback.exponent - currentCellHeight / callback.exponent)).toFloat(),
            null
        )
        if (isMoving && currentPosition == desPosition && currentCellWidth == defaultCellWidth) {
            isMoving = false
            if (increased) {
                callback.updateScore(getValue())
                increaseValue()
            }
            callback.finishedMoving(this)
        }
    }

    fun needsToUpdate(): Boolean = currentPosition != desPosition || currentCellWidth != defaultCellWidth

    fun isSolidGone(): Boolean = isSolidGone

    private fun removeSolidBlock() {
        val sizeSpeed = 20
        if (currentCellHeight - sizeSpeed <= 0 || currentCellWidth - sizeSpeed <= 0) {
            isSolidGone = true
        }
        currentCellHeight -= sizeSpeed
        currentCellWidth -= sizeSpeed
    }
}
