package com.rossyn.blocktiles.game2048.presentation.components

import android.graphics.Bitmap
import android.graphics.Canvas
import com.rossyn.blocktiles.game2048.domain.models.Position

class Tile {
    private lateinit var callback: GameBoardView
    var value: Long = 0L
    private var winningValue = 0
    private var solidLives = 0

    private var currentPositionX: Int = 0
    private var currentPositionY: Int = 0
    private var desPositionX: Int = 0
    private var desPositionY: Int = 0

    private var currentCellHeight: Int = 0
    private var currentCellWidth: Int = 0
    private var defaultCellHeight: Int = 0
    private var defaultCellWidth: Int = 0
    private var currentPosition: Position = Position(0, 0)
    private var desPosition: Position = Position(0, 0)
    private var isMoving = false
    private var increased = false
    var isSolid = false
    private var isSolidGone = false
    private var bitmapCreator = BitmapCreator()


    constructor(value: Long, position: Position, callback: GameBoardView) {
        this.value = value
        this.callback = callback
        this.winningValue = callback.winningValue

        defaultCellHeight = bitmapCreator.getCellDefaultHeight()
        currentCellHeight = defaultCellHeight
        defaultCellWidth = bitmapCreator.getCellDefaultWidth()
        currentCellWidth = defaultCellWidth
        currentPosition = position
        desPosition = currentPosition
        currentPositionX = currentPosition.positionX
        desPositionX = currentPositionX
        currentPositionY = currentPosition.positionY
        desPositionY = currentPositionY
        currentCellHeight /= 5
        currentCellWidth /= 5
    }

    constructor(value: Int, position: Position, callback: GameBoardView, solidLives: Int) {
        this.value = value.toLong()
        this.callback = callback

        defaultCellHeight = bitmapCreator.getCellDefaultHeight()
        currentCellHeight = defaultCellHeight
        defaultCellWidth = bitmapCreator.getCellDefaultWidth()
        currentCellWidth = defaultCellWidth
        currentPosition = position
        desPosition = currentPosition
        currentPositionX = currentPosition.positionX
        desPositionX = currentPositionX
        currentPositionY = currentPosition.positionY
        desPositionY = currentPositionY
        currentCellHeight /= 5
        currentCellWidth /= 5

        this.isSolid = true
        this.solidLives = solidLives
    }



    fun getPosition(): Position {
        return currentPosition

    }


    fun decreaseLiveCount() {
        solidLives--
    }

    fun move(position: Position) {
        desPosition = position
        desPositionX = desPosition.positionX
        desPositionY = desPosition.positionY
        isMoving = true
    }

    fun notAlreadyIncreased(): Boolean {
        return !increased
    }

    fun setIncreased(state: Boolean) {
        increased = state
    }

    fun copyTile(): Tile {
        return Tile(this.value, this.currentPosition, this.callback)
    }

    fun increaseValue() {
        this.value *= callback.getExponent()
        currentCellHeight = (defaultCellHeight * 1.4).toInt()
        currentCellWidth = (defaultCellWidth * 1.4).toInt()
        increased = false
        if (value == winningValue.toLong()) {
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
            (currentPositionX + (defaultCellWidth / callback.getExponent() - currentCellWidth / callback.getExponent())).toFloat(),
            (currentPositionY + (defaultCellHeight / callback.getExponent() - currentCellHeight / callback.getExponent())).toFloat(),
            null
        )
        if (isMoving && currentPosition == desPosition && currentCellWidth == defaultCellWidth) {
            isMoving = false
            if (increased) {
                callback.updateScore(value)
                increaseValue()
            }
            callback.finishedMoving(this)
        }
    }

    fun needsToUpdate(): Boolean {
        return currentPosition != desPosition || currentCellWidth != defaultCellWidth

    }

    fun isSolidGone(): Boolean {
        return isSolidGone
    }

    private fun removeSolidBlock() {
        val sizeSpeed = 20
        if (currentCellHeight - sizeSpeed <= 0 || currentCellWidth - sizeSpeed <= 0) {
            isSolidGone = true
        }

        currentCellHeight -= sizeSpeed
        currentCellWidth -= sizeSpeed
    }
}
