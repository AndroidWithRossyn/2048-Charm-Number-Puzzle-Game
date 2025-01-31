package com.rossyn.blocktiles.game2048.presentation.components

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.os.CountDownTimer
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.rossyn.blocktiles.game2048.R
import com.rossyn.blocktiles.game2048.data.prefs.Scores
import com.rossyn.blocktiles.game2048.presentation.activities.GameActivity
import com.rossyn.blocktiles.game2048.presentation.interfaces.GameInterface
import com.rossyn.blocktiles.game2048.utils.Constants
import com.rossyn.blocktiles.game2048.utils.getDrawableCompat


class GameViewCell(context: Context, attrs: AttributeSet) : SurfaceView(context, attrs),
    SurfaceHolder.Callback {
    private var swipe: MediaPlayer? = null

    private lateinit var thread: ThreadMain

    private var isInit = false

    var isTutorial: Boolean = false
    var isWinningMsgPlayed: Boolean = false
    var isNewScoreMsgPlayed: Boolean = false
    var isTutorialFromMainScreen = false

    private var scores: Scores

    private var gameBoardView: GameBoardView
    var dialogOpen = false
    private val backgroundRectangle: Drawable =
        context.getDrawableCompat(R.drawable.game_background)!!
    private val cellRectangle: Drawable = context.getDrawableCompat(R.drawable.cell_shape)!!


    private var builder: AlertDialog.Builder
    private var gameActivity: GameActivity
    private var gameOverDialog: Dialog

    init {
        val dm = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay.getMetrics(dm)

        holder.addCallback(this)
        setZOrderOnTop(true) // necessary
        holder.setFormat(PixelFormat.TRANSPARENT)

        this.gameActivity = (context as GameActivity)
        swipe = MediaPlayer.create(gameActivity, R.raw.swipe)

        isInit = false
        val exponent = GameActivity.boardExponent
        val rows = GameActivity.boardRows
        val cols = GameActivity.boardCols
        val gameMode = GameActivity.gameMode

        isTutorial = gameActivity.isTutorial()
        isTutorialFromMainScreen = gameActivity.isTutorialFromMainScreen
        scores = Scores(
            0L,
            context.getSharedPreferences(
                Constants.SHARED_PREFERENCE_FILE_NAME,
                Context.MODE_PRIVATE
            ),
            gameMode,
            rows,
            cols
        )


        gameBoardView = GameBoardView(rows, cols, exponent, this, gameMode)
        BitmapCreator.exponent = exponent
        builder = AlertDialog.Builder(context)
        gameOverDialog = Dialog(context)
    }

    private var gameInterface: GameInterface? = null

    fun addCallback(gameInterface: GameInterface) {
        this.gameInterface = gameInterface
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
//        gameInterface?.surfaceCreated(holder)

        thread = ThreadMain(holder, this)
        gameActivity.setThread(thread)
        thread.setRunning(true)
        thread.start()

        gameActivity.updateScore(scores.score, scores.TopScore)
        initBarButtons()

        prepareGameOverDialog()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
//        gameInterface?.surfaceChanged(holder)
        thread.setSurfaceHolder(holder)
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
//        gameInterface?.surfaceDestroyed()

        var retry = true
        while (retry) {
            try {
                thread.setRunning(false)
                thread.join()
                retry = false
                BitmapCreator.bitmapArrayList.clear()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }


    fun update() {
        if (gameBoardView.isGameOver() && !dialogOpen) {
            dialogOpen = true
            gameOverDialog()
        }

        if (scores.isNewHighScore()) {
            scores.updateScoreBoard()
            scores.refreshScoreBoard()

            if (!isNewScoreMsgPlayed) {
                showAnnouncingMsg(context.getString(R.string.new_score))
                isNewScoreMsgPlayed = true
            }
        }
        if (!isWinningMsgPlayed && gameBoardView.isGameWon()) {
            showAnnouncingMsg(context.getString(R.string.winner))
            isWinningMsgPlayed = true
        }
        if (isInit) {
            gameBoardView.update()
        }
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        drawDrawable(canvas, backgroundRectangle, 0, 0, width, height)
        drawEmptyBoard(canvas)
        if (!isInit) {
            if (isTutorial) {
                firstScreenTutorial()
                gameBoardView.initTutorialBoard()
            } else {
                gameBoardView.initBoard()
            }
        }
        isInit = true
        gameBoardView.draw(canvas)
    }

    fun updateScore(value: Long) {
        scores.updateScore(value)
        gameActivity.updateScore(scores.score, scores.TopScore)
    }

    private fun drawEmptyBoard(canvas: Canvas) {
        drawDrawable(canvas, backgroundRectangle, 0, 0, width, height)

        val padding = pxFromDp(3.0f).toInt()
        val boardWidth = width - padding * 2
        val boardHeight = height - padding * 2

        val cellWidth = boardWidth / gameBoardView.getCols()
        val cellHeight = boardHeight / gameBoardView.getRows()

        BitmapCreator.cellDefaultHeight = cellHeight
        BitmapCreator.cellDefaultWidth = cellWidth


        for (x in 0 until gameBoardView.getCols()) {
            for (y in 0 until gameBoardView.getRows()) {

                val posX = x * cellWidth + padding
                val posY = y * cellHeight + padding

                val posXX = posX + cellWidth
                val posYY = posY + cellHeight

                cellRectangle.setColorFilter(
                    context.getColor(R.color.valueEmpty),
                    PorterDuff.Mode.SRC_OVER
                )
                drawDrawable(canvas, cellRectangle, posX, posY, posXX, posYY)

                if (!isInit) {
                    gameBoardView.setPositions(y, x, posX, posY)
                }
            }
        }
    }

    private fun drawDrawable(
        canvas: Canvas,
        draw: Drawable,
        startingX: Int,
        startingY: Int,
        endingX: Int,
        endingY: Int
    ) {
        draw.setBounds(startingX, startingY, endingX, endingY)
        draw.draw(canvas)
    }

    fun initSwipeListener(view: View) {
        view.setOnTouchListener(object : OnSwipeTouchListener(view.context) {
            override fun onSwipeTop() {
                if (!dialogOpen) {
                    gameBoardView.up()
                    secondScreenTutorial()
                }
            }

            override fun onSwipeRight() {
                if (!dialogOpen) {
                    gameBoardView.right()
                    secondScreenTutorial()
                }
            }

            override fun onSwipeLeft() {
                if (!dialogOpen) {
                    gameBoardView.left()
                    secondScreenTutorial()
                }
            }

            override fun onSwipeBottom() {
                if (!dialogOpen) {
                    gameBoardView.down()
                    secondScreenTutorial()
                }
            }
        })
    }

    private fun initBarButtons() {
        val resetBtn: ImageButton = gameActivity.findViewById(R.id.ib_reset)
        resetBtn.setOnClickListener {
            if (!isTutorial) {
                playClick()
                if (scores.isNewHighScore()) scores.updateScoreBoard()
                scores.refreshScoreBoard()
                gameBoardView.resetGame()
                scores.resetGame()
                gameActivity.updateScore(scores.score, scores.TopScore)
                isNewScoreMsgPlayed = false
                isWinningMsgPlayed = false
            }
        }
        val undoBtn: ImageButton = gameActivity.findViewById(R.id.ib_undo)
        undoBtn.setOnClickListener {
            if (!isTutorial) {
                playClick()
                gameBoardView.undoMove()
                scores.undoScore()
                gameActivity.updateScore(scores.score, scores.TopScore)
            }
        }
    }

    fun prepareGameOverDialog() {
        gameOverDialog.setContentView(R.layout.dialog_gameover)
        gameOverDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        gameOverDialog.setCancelable(false)

        val scaleAnim = AnimationUtils.loadAnimation(gameActivity, R.anim.scale_animation)
        val playAgainBtn: Button = gameOverDialog.findViewById(R.id.btn_play_again)
        playAgainBtn.startAnimation(scaleAnim)
        playAgainBtn.setOnClickListener {
            playClick()
            gameBoardView.resetGame()
            gameOverDialog.dismiss()
            dialogOpen = false
        }
    }

    private fun gameOverDialog() {
        gameActivity.runOnUiThread {
            val tvScore: TextView = gameOverDialog.findViewById(R.id.game_over_score_num)
            tvScore.text = scores.score.toString()
            gameOverDialog.show()
        }
    }


    fun ShowShufflingMsg() {
        val shufflingBackground: ImageView = gameActivity.findViewById(R.id.background_dark)
        val shufflingText: TextView = gameActivity.findViewById(R.id.announcing_msg)

        gameActivity.runOnUiThread {
            dialogOpen = true
            shufflingText.text = context.getString(R.string.shuffle)
            shufflingText.setTextColor(context.getColor(R.color.value2))
            shufflingText.visibility = VISIBLE
            shufflingBackground.visibility = VISIBLE
            object : CountDownTimer(1000, 100) {
                override fun onTick(millisUntilFinished: Long) {}

                override fun onFinish() {
                    shufflingText.visibility = GONE
                    shufflingBackground.visibility = GONE
                    dialogOpen = false
                }
            }.start()
        }
    }

    fun firstScreenTutorial() {
        val tutorialBackground: ImageView = gameActivity.findViewById(R.id.background_dark)
        val tutorialText: TextView = gameActivity.findViewById(R.id.tutorial_textview)
        gameActivity.runOnUiThread {
            tutorialBackground.visibility = VISIBLE
            tutorialText.visibility = VISIBLE
            tutorialText.text = gameActivity.getString(R.string.tutorial_first_line)
        }
    }

    fun secondScreenTutorial() {
        val tutorialText: TextView = gameActivity.findViewById(R.id.tutorial_textview)
        gameActivity.runOnUiThread {
            tutorialText.text = gameActivity.getString(R.string.tutorial_second_line)
        }
    }

    fun thirdScreenTutorial() {
        val tutorialBackground: ImageView = gameActivity.findViewById(R.id.background_dark)
        val tutorialText: TextView = gameActivity.findViewById(R.id.tutorial_textview)
        val endBtn: Button = gameActivity.findViewById(R.id.button_end_tutorial)
        val scaleAnim = AnimationUtils.loadAnimation(gameActivity, R.anim.scale_animation)
        endBtn.startAnimation(scaleAnim)
        gameActivity.runOnUiThread {
            tutorialText.text = gameActivity.getString(R.string.tutorial_third_line)
            endBtn.visibility = VISIBLE
            dialogOpen = true
            endBtn.setOnClickListener {
                if (isTutorialFromMainScreen) {
                    gameActivity.onBackPressedCustom()
                } else {
                    playClick()
                    endBtn.clearAnimation()
                    tutorialBackground.visibility = GONE
                    tutorialText.visibility = GONE
                    endBtn.visibility = GONE
                    gameBoardView.setTutorialFinished()
                    isTutorial = false
                    dialogOpen = false
                }
            }
        }
    }


    fun showAnnouncingMsg(msg: String) {
        val msgBackground: ImageView = gameActivity.findViewById(R.id.background_dark)
        val msgText: TextView = gameActivity.findViewById(R.id.announcing_msg)
        gameActivity.runOnUiThread {
            msgText.text = msg
            dialogOpen = true
            msgText.visibility = VISIBLE
            msgBackground.visibility = VISIBLE
            msgText.textSize = 60f

            object : CountDownTimer(2000, 100) {
                var count = 0

                override fun onTick(millisUntilFinished: Long) {
                    when (count) {
                        0 -> {
                            msgText.setTextColor(context.getColor(R.color.value2))
                            count++
                        }

                        1 -> {
                            msgText.setTextColor(context.getColor(R.color.value4))
                            count++
                        }

                        2 -> {
                            msgText.setTextColor(context.getColor(R.color.value8))
                            count++
                        }

                        else -> {
                            msgText.setTextColor(context.getColor(R.color.value16))
                            count = 0
                        }
                    }
                }

                override fun onFinish() {
                    msgText.visibility = GONE
                    msgBackground.visibility = GONE
                    dialogOpen = false
                }
            }.start()
        }
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }

    fun pxFromDp(dp: Float): Float {
        return dp * context.resources.displayMetrics.density
    }

    fun playClick() {
        val click = MediaPlayer.create(gameActivity, R.raw.click)
        if (gameActivity.isSoundPlayed()) {
            click.start()
        }
    }

    fun playSwipe() {
        if (gameActivity.isSoundPlayed()) {
            swipe?.start()
        }
    }


}
