package com.rossyn.blocktiles.game2048.presentation.components

import android.graphics.Canvas
import com.rossyn.blocktiles.game2048.domain.models.Position
import com.rossyn.blocktiles.game2048.presentation.components.BitmapCreator.Companion.exponent
import java.util.Random
import kotlin.math.ln

class GameBoardView(
    private val boardRows: Int,
    private val boardCols: Int,
    private val exponentValue: Int,
    private val callback: GameViewCell,
    private val gameMode: Int
) {

    companion object {
        const val GAME_MODE_SOLID_TILE = 1
        const val GAME_MODE_SHUFFLE = 2
        const val NUM_SOLID_LIVES = 10
    }

    private var tempBoard: Array<Array<Tile?>> = Array(boardRows) { arrayOfNulls(boardCols) }
    private var board: Array<Array<Tile?>> = Array(boardRows) { arrayOfNulls(boardCols) }
    private var oldBoard: Array<Array<Tile?>> = Array(boardRows) { arrayOfNulls(boardCols) }
    private val positions: Array<Array<Position>> =
        Array(boardRows) { Array(boardCols) { Position(0, 0) } }

    private val rand = Random()

    private var gameOver = false
    private var gameWon = false
    private var isMoving = false
    private var spawnNeeded = false
    private var canUndo = false
    private var boardIsInitialized = false
    private var tutorialIsPlaying = false
    private var movingTiles = mutableListOf<Tile>()
    private var currentScore: Long = 0
    private var oldScore: Long = 0
    private val winningValue: Int = Math.pow(exponentValue.toDouble(), 11.0).toInt()

    init {
        addRandom()
        addRandom()
    }

    fun isGameOver() = gameOver

    fun getTile(x: Int, y: Int): Tile? = board[x][y]

    fun isGameWon() = gameWon

    fun getExponent() = exponentValue

    fun getRows() = boardRows

    fun getCols() = boardCols

    fun setPositions(matrixX: Int, matrixY: Int, positionX: Int, positionY: Int) {
        positions[matrixX][matrixY] = Position(positionX, positionY)
    }

    fun setPlayerWon() {
        gameWon = true
    }

    fun getWinningValue() = winningValue

    fun initBoard() {
        if (!boardIsInitialized) {
            addRandom()
            addRandom()
            movingTiles.clear()
            boardIsInitialized = true
        }
    }

    fun initTutorialBoard() {
        tutorialIsPlaying = true
        board[0][0] = Tile(exponentValue.toLong(), positions[0][0], this)
        board[1][2] = Tile(exponentValue.toLong(), positions[1][2], this)
        movingTiles.clear()
        boardIsInitialized = true
    }

    fun setTutorialFinished() {
        tutorialIsPlaying = false
        resetGame()
    }

    fun draw(canvas: Canvas) {
        for (x in 0 until boardRows) {
            for (y in 0 until boardCols) {
                board[x][y]?.draw(canvas)
            }
        }
    }

    fun update() {
        var updating = false
        for (x in 0 until boardRows) {
            for (y in 0 until boardCols) {
                board[x][y]?.update()
                if (board[x][y]?.isSolidGone() == true) {
                    board[x][y] = null
                    break
                }
                if (board[x][y]?.needsToUpdate() == true) {
                    updating = true
                }
            }
        }
        if (!updating) {
            checkIfGameOver()
        }
    }

    private fun addRandom() {
        var count = 0
        for (x in 0 until boardRows) {
            for (y in 0 until boardCols) {
                if (getTile(x, y) == null) count++
            }
        }
        val number = rand.nextInt(count)
        count = 0
        for (x in 0 until boardRows) {
            for (y in 0 until boardCols) {
                if (getTile(x, y) == null) {
                    if (count == number) {
                        board[x][y] = Tile(exponentValue.toLong(), positions[x][y], this)
                        return
                    }
                    count++
                }
            }
        }
    }

    fun up() {
        saveBoardState()
        if (!isMoving) {
            isMoving = true
            tempBoard = Array(boardRows) { arrayOfNulls<Tile>(boardCols) }

            for (x in 0 until boardRows) {
                for (y in 0 until boardCols) {
                    board[x][y]?.let {
                        tempBoard[x][y] = it
                        for (k in x - 1 downTo 0) {
                            if (tempBoard[k][y] == null) {
                                tempBoard[k][y] = it
                                if (tempBoard[k + 1][y] == it) {
                                    tempBoard[k + 1][y] = null
                                }
                            } else if (tempBoard[k][y]?.value == it.value &&
                                !tempBoard[k][y]!!.notAlreadyIncreased() && it.value.toInt() != 1) {
                                tempBoard[k][y] = it
                                tempBoard[k][y]?.setIncreased(true)
                                if (tempBoard[k + 1][y] == it) {
                                    tempBoard[k + 1][y] = null
                                }
                            } else {
                                break
                            }
                        }
                    }
                }
            }
            moveTiles()
            board = tempBoard
        }
    }

    fun left() {
        saveBoardState()
        if (!isMoving) {
            isMoving = true
            tempBoard = Array(boardRows) { arrayOfNulls<Tile>(boardCols) }

            for (x in 0 until boardRows) {
                for (y in 0 until boardCols) {
                    board[x][y]?.let {
                        tempBoard[x][y] = it
                        for (k in y - 1 downTo 0) {
                            if (tempBoard[x][k] == null) {
                                tempBoard[x][k] = it
                                if (tempBoard[x][k + 1] == it) {
                                    tempBoard[x][k + 1] = null
                                }
                            } else if (tempBoard[x][k]?.value == it.value &&
                                !tempBoard[x][k]!!.notAlreadyIncreased() && it.value.toInt() != 1) {
                                tempBoard[x][k] = it
                                tempBoard[x][k]?.setIncreased(true)
                                if (tempBoard[x][k + 1] == it) {
                                    tempBoard[x][k + 1] = null
                                }
                            } else {
                                break
                            }
                        }
                    }
                }
            }
            moveTiles()
            board = tempBoard
        }
    }

    fun down() {
        saveBoardState()
        if (!isMoving) {
            isMoving = true
            tempBoard = Array(boardRows) { arrayOfNulls<Tile>(boardCols) }

            for (x in boardRows - 1 downTo 0) {
                for (y in 0 until boardCols) {
                    board[x][y]?.let {
                        tempBoard[x][y] = it
                        for (k in x + 1 until boardRows) {
                            if (tempBoard[k][y] == null) {
                                tempBoard[k][y] = it
                                if (tempBoard[k - 1][y] == it) {
                                    tempBoard[k - 1][y] = null
                                }
                            } else if (tempBoard[k][y]?.value == it.value &&
                                !tempBoard[k][y]!!.notAlreadyIncreased() && it.value.toInt() != 1) {
                                tempBoard[k][y] = it
                                tempBoard[k][y]?.setIncreased(true)
                                if (tempBoard[k - 1][y] == it) {
                                    tempBoard[k - 1][y] = null
                                }
                            } else {
                                break
                            }
                        }
                    }
                }
            }
            moveTiles()
            board = tempBoard
        }
    }

    fun right() {
        saveBoardState()
        if (!isMoving) {
            isMoving = true
            tempBoard = Array(boardRows) { arrayOfNulls<Tile>(boardCols) }

            for (x in 0 until boardRows) {
                for (y in boardCols - 1 downTo 0) {
                    board[x][y]?.let {
                        tempBoard[x][y] = it
                        for (k in y + 1 until boardCols) {
                            if (tempBoard[x][k] == null) {
                                tempBoard[x][k] = it
                                if (tempBoard[x][k - 1] == it) {
                                    tempBoard[x][k - 1] = null
                                }
                            } else if (tempBoard[x][k]?.value == it.value &&
                                !tempBoard[x][k]!!.notAlreadyIncreased() && it.value.toInt() != 1) {
                                tempBoard[x][k] = it
                                tempBoard[x][k]?.setIncreased(true)
                                if (tempBoard[x][k - 1] == it) {
                                    tempBoard[x][k - 1] = null
                                }
                            } else {
                                break
                            }
                        }
                    }
                }
            }
            moveTiles()
            board = tempBoard
        }
    }











    private fun moveTiles() {
        for (x in 0 until boardRows) {
            for (y in 0 until boardCols) {
                val t = tempBoard[x][y]
                t?.let {
                    if (it.getPosition() != positions[x][y]) {
                        movingTiles.add(it)
                        it.move(positions[x][y])
                    }
                }
            }
        }
        if (movingTiles.isEmpty()) {
            isMoving = false
        } else {
            spawnNeeded = true
        }
    }

    fun spawn() {
        if (spawnNeeded) {
            addRandom()
            spawnNeeded = false
        }
    }

    fun finishedMoving(t: Tile) {
        movingTiles.remove(t)
        if (movingTiles.isEmpty()) {
            callback.playSwipe()
            callback.updateScore(currentScore)
            isMoving = false
            spawn()
            if (gameMode == GAME_MODE_SHUFFLE && !tutorialIsPlaying) shuffleBoard()
            if (gameMode == GAME_MODE_SOLID_TILE && !tutorialIsPlaying) {
                decreaseSolidLives()
                addRandomSolidTile()
            }
        }
    }

    private fun checkIfGameOver() {
        gameOver = true
        for (x in 0 until boardRows) {
            for (y in 0 until boardCols) {
                if (board[x][y] == null) {
                    gameOver = false
                    break
                }
            }
        }
        if (gameOver) {
            for (x in 0 until boardRows) {
                for (y in 0 until boardCols) {
                    if ((x > 0 && board[x - 1][y]?.value == board[x][y]?.value && board[x][y]?.value?.toInt() != 1) ||
                        (x < boardRows - 1 && board[x + 1][y]?.value == board[x][y]?.value && board[x][y]?.value?.toInt() != 1) ||
                        (y > 0 && board[x][y - 1]?.value == board[x][y]?.value && board[x][y]?.value?.toInt() != 1) ||
                        (y < boardCols - 1 && board[x][y + 1]?.value == board[x][y]?.value && board[x][y]?.value?.toInt() != 1)
                    ) {
                        gameOver = false
                        break
                    }
                }
            }
        }
    }

    fun updateScore(value: Long) {
        if (tutorialIsPlaying) {
            callback.thirdScreenTutorial()
        } else {
            var value1 = ln(value.toDouble()) / ln(exponent.toDouble())
            value1 = (Math.round(value1) + 1).toDouble()
            val score = Math.pow(2.0, value1.toDouble()).toInt().toLong()
            currentScore += score.toLong()
        }
    }

    private fun saveBoardState() {
        canUndo = true
        oldBoard = Array(boardRows) { arrayOfNulls<Tile>(boardCols) }
        for (x in 0 until boardRows) {
            for (y in 0 until boardCols) {
                board[x][y]?.let {
                    oldBoard[x][y] = it.copyTile()
                }
            }
        }
        oldScore = currentScore
    }

    fun undoMove() {
        if (canUndo) {
            board = oldBoard
            currentScore = oldScore
            canUndo = false
        }
    }

    fun resetGame() {
        gameOver = false
        gameWon = false
        canUndo = false
        for (x in 0 until boardRows) {
            for (y in 0 until boardCols) {
                board[x][y] = null
            }
        }
        currentScore = 0
        addRandom()
        addRandom()
    }

    private fun shuffleBoard() {
        val num = rand.nextInt(100)
        if (num in 0..5) {
            callback.ShowShufflingMsg()
            startShuffle()
        }
    }

    private fun startShuffle() {
        val newBoard = Array(boardRows) { arrayOfNulls<Tile>(boardCols) }
        var moved: Boolean
        for (x in 0 until boardRows) {
            for (y in 0 until boardCols) {
                board[x][y]?.let {
                    moved = false
                    while (!moved) {
                        val randX = rand.nextInt(boardRows)
                        val randY = rand.nextInt(boardCols)
                        if (newBoard[randX][randY] == null) {
                            newBoard[randX][randY] = Tile(it.value, positions[randX][randY], this)
                            moved = true
                        }
                    }
                }
            }
        }
        board = newBoard
    }

    private fun addRandomSolidTile() {
        val num = rand.nextInt(100)
        if (num in 0..5) {
            var count = 0
            for (x in 0 until boardRows) {
                for (y in 0 until boardCols) {
                    if (getTile(x, y) == null) count++
                }
            }
            val number = rand.nextInt(count)
            count = 0
            for (x in 0 until boardRows) {
                for (y in 0 until boardCols) {
                    if (getTile(x, y) == null) {
                        if (count == number) {
                            board[x][y] = Tile(exponentValue.toLong(), positions[x][y], this)
                            return
                        }
                        count++
                    }
                }
            }
        }
    }

    private fun decreaseSolidLives() {
        for (x in 0 until boardRows) {
            for (y in 0 until boardCols) {
                if (board[x][y] != null && board[x][y]!!.isSolid()) board[x][y]!!.decreaseLiveCount()
            }
        }
    }
}
