package com.rossyn.blocktiles.game2048.presentation.components

import android.graphics.Canvas
import com.rossyn.blocktiles.game2048.domain.models.Boards
import com.rossyn.blocktiles.game2048.domain.models.Exponent
import com.rossyn.blocktiles.game2048.domain.models.Position
import java.util.Random
import kotlin.math.ln

class GameBoardView(
    private val rows_x: Int = Boards.BOARD_4x4.rows,
    private val cols_x: Int = Boards.BOARD_4x4.cols,
    private val exponentValue_x: Int = Exponent.Two.value,
    private val callback: GameViewCell,
    private val gameMode: Int = 0
) {

    val GAME_MODE_SOLID_TILE = 1
    val GAME_MODE_SHUFFLE = 2
    val NUM_SOLID_LIVES = 10

    private var boardRows = rows_x
    private var boardCols = cols_x
    private var exponent = exponentValue_x

    private lateinit var tempBoard: Array<Array<Tile?>>
    private lateinit var board: Array<Array<Tile?>>

    private lateinit var oldBoard: Array<Array<Tile?>>
    private lateinit var positions: Array<Array<Position?>>


    var winningValue: Int = Math.pow(exponent.toDouble(), 11.0).toInt()
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


    init {
        boardIsInitialized = false
        board = Array(boardRows) { arrayOfNulls<Tile?>(boardCols) }
        positions = Array(boardRows) { arrayOfNulls<Position?>(boardCols) }
    }

    fun isGameOver(): Boolean {
        return gameOver
    }

    fun getTile(x: Int, y: Int): Tile? {
        return board[x][y]
    }

    fun isGameWon(): Boolean {
        return gameWon
    }

    fun getExponent(): Int {
        return exponent
    }

    fun getRows(): Int {
        return boardRows
    }

    fun getCols(): Int {
        return boardCols
    }

    fun setPositions(matrixX: Int, matrixY: Int, positionX: Int, positionY: Int) {
        positions[matrixX][matrixY] = Position(positionX, positionY)
    }

    fun setPlayerWon() {
        gameWon = true
    }


    fun initBoard() {
        if (!boardIsInitialized) {
            addRandom() // tile one
            addRandom() // tile two
            movingTiles = mutableListOf<Tile>()
            boardIsInitialized = true
        }
    }

    fun initTutorialBoard() {
        tutorialIsPlaying = true
        board[0][0] = Tile(exponent.toLong(), positions[0][0]!!, this) // tile one
        board[1][2] = Tile(exponent.toLong(), positions[1][2]!!, this) // tile two
        movingTiles = mutableListOf<Tile>()
        boardIsInitialized = true
    }

    fun setTutorialFinished() {
        tutorialIsPlaying = false
        resetGame()
    }

    fun draw(canvas: Canvas) {
        for (x in 0 until boardRows) {
            for (y in 0 until boardCols) {
                if (board[x][y] != null) {
                    board[x][y]!!.draw(canvas)
                }
            }
        }
    }

    fun update() {
        var updating = false
        for (x in 0 until boardRows) {
            for (y in 0 until boardCols) {
                if (board[x][y] != null) {
                    board[x][y]!!.update()
                    if (board[x][y]!!.isSolidGone()) {
                        board[x][y] = null
                        break
                    }
                    if (board[x][y]!!.needsToUpdate()) {
                        updating = true
                    }
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
                        board[x][y] = Tile(exponent.toLong(), positions[x][y]!!, this)
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
            tempBoard = Array(boardRows) { arrayOfNulls<Tile?>(boardCols) }

            for (x in 0 until boardRows) {
                for (y in 0 until boardCols) {
                    if (board[x][y] != null) {
                        tempBoard[x][y] = board[x][y]
                        for (k in x - 1 downTo 0) {
                            if (tempBoard[k][y] == null) {
                                tempBoard[k][y] = board[x][y]
                                if (tempBoard[k + 1][y] === board[x][y]) {
                                    tempBoard[k + 1][y] = null
                                }
                            } else if (tempBoard[k][y]!!.value == board[x][y]!!.value && tempBoard[k][y]!!
                                    .notAlreadyIncreased() && board[x][y]!!.value.toInt() != 1
                            ) {
                                tempBoard[k][y] = board[x][y]
                                tempBoard[k][y]!!.setIncreased(true)
                                if (tempBoard[k + 1][y] === board[x][y]) {
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
            tempBoard = Array(boardRows) { arrayOfNulls<Tile?>(boardCols) }

            for (x in 0 until boardRows) {
                for (y in 0 until boardCols) {
                    if (board[x][y] != null) {
                        tempBoard[x][y] = board[x][y]
                        for (k in y - 1 downTo 0) {
                            if (tempBoard[x][k] == null) {
                                tempBoard[x][k] = board[x][y]
                                if (tempBoard[x][k + 1] == board[x][y]) {
                                    tempBoard[x][k + 1] = null
                                }
                            } else if (tempBoard[x][k]?.value == board[x][y]!!.value &&
                                !tempBoard[x][k]!!.notAlreadyIncreased() && board[x][y]!!.value.toInt() != 1
                            ) {
                                tempBoard[x][k] = board[x][y]
                                tempBoard[x][k]?.setIncreased(true)
                                if (tempBoard[x][k + 1] == board[x][y]) {
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
            tempBoard = Array(boardRows) { arrayOfNulls<Tile?>(boardCols) }

            for (x in boardRows - 1 downTo 0) {
                for (y in 0 until boardCols) {
                    if (board[x][y] != null) {
                        tempBoard[x][y] = board[x][y]
                        for (k in x + 1 until boardRows) {
                            if (tempBoard[k][y] == null) {
                                tempBoard[k][y] = board[x][y]
                                if (tempBoard[k - 1][y] == board[x][y]) {
                                    tempBoard[k - 1][y] = null
                                }
                            } else if (tempBoard[k][y]?.value == board[x][y]!!.value &&
                                !tempBoard[k][y]!!.notAlreadyIncreased() && board[x][y]!!.value.toInt() != 1
                            ) {
                                tempBoard[k][y] = board[x][y]
                                tempBoard[k][y]?.setIncreased(true)
                                if (tempBoard[k - 1][y] == board[x][y]) {
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
            tempBoard = Array(boardRows) { arrayOfNulls<Tile?>(boardCols) }

            for (x in 0 until boardRows) {
                for (y in boardCols - 1 downTo 0) {
                    if (board[x][y] != null) {
                        tempBoard[x][y] = board[x][y]
                        for (k in y + 1 until boardCols) {
                            if (tempBoard[x][k] == null) {
                                tempBoard[x][k] = board[x][y]
                                if (tempBoard[x][k - 1] == board[x][y]) {
                                    tempBoard[x][k - 1] = null
                                }
                            } else if (tempBoard[x][k]?.value == board[x][y]!!.value &&
                                !tempBoard[x][k]!!.notAlreadyIncreased() && board[x][y]!!.value.toInt() != 1
                            ) {
                                tempBoard[x][k] = board[x][y]
                                tempBoard[x][k]?.setIncreased(true)
                                if (tempBoard[x][k - 1] == board[x][y]) {
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
                if (t != null) {
                    if (t.getPosition() !== positions[x][y]) {
                        movingTiles.add(t)
                        t.move(positions[x][y]!!)
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
            if (gameMode == GAME_MODE_SHUFFLE && !tutorialIsPlaying) {
                shuffleBoard()
            }
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
                if (board[x][y] != null) {
                    oldBoard[x][y] = board[x][y]!!.copyTile()
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

    fun startShuffle() {
        val newBoard = Array(boardRows) { arrayOfNulls<Tile>(boardCols) }

        var randX: Int
        var randY: Int
        var moved: Boolean

        for (x in 0 until boardRows) {
            for (y in 0 until boardCols) {
                if (getTile(x, y) != null) {
                    moved = false
                    while (!moved) {
                        randX = rand.nextInt(boardRows)
                        randY = rand.nextInt(boardCols)

                        if (newBoard[randX][randY] == null) {
                            newBoard[randX][randY] =
                                Tile(board[x][y]!!.value, positions[randX][randY]!!, this)
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
                            board[x][y] = Tile(
                                1,
                                positions[x][y]!!,
                                this,
                                NUM_SOLID_LIVES
                            ) // here we need to send a special value and set a bitmap to look like a solid block, value should be a special one that can never be merged with
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
                if (board[x][y] != null && board[x][y]!!.isSolid) board[x][y]!!.decreaseLiveCount()
            }
        }
    }
}
