package com.rossyn.blocktiles.game2048.presentation.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.view.SurfaceHolder
import android.view.View
import androidx.core.view.ViewCompat
import com.rossyn.blocktiles.game2048.R
import com.rossyn.blocktiles.game2048.databinding.GameActivityBinding
import com.rossyn.blocktiles.game2048.domain.models.Boards
import com.rossyn.blocktiles.game2048.domain.models.Exponent
import com.rossyn.blocktiles.game2048.presentation.components.GameViewCell
import com.rossyn.blocktiles.game2048.presentation.components.ThreadMain
import com.rossyn.blocktiles.game2048.presentation.interfaces.GameInterface
import com.rossyn.blocktiles.game2048.presentation.utils.enableEdgeToEdgeAppTrans
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GameActivity : BaseActivity(), GameInterface, View.OnClickListener {


    override fun onBackPressedCustom() {
        finish()
    }

    companion object {
        var boardRows = Boards.BOARD_4x4.rows
        var boardCols: Int = Boards.BOARD_4x4.cols
        var boardExponent: Int = Exponent.Two.value
        var gameMode: Int = 0
    }

    var isTutorialFromMainScreen = false


    private lateinit var binding: GameActivityBinding

    private lateinit var gameViewCell: GameViewCell

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // need to set all values before layout init
        boardRows = intent.getIntExtra("rows", Boards.BOARD_4x4.rows)
        boardCols = intent.getIntExtra("cols", Boards.BOARD_4x4.cols)
        boardExponent = intent.getIntExtra("exponent", Exponent.Two.value)
        gameMode = intent.getIntExtra("game_mode", 0)
        isTutorialFromMainScreen = intent.getBooleanExtra("tutorial", false)

        enableEdgeToEdgeAppTrans()
        binding = GameActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            v.setPadding(0, 0, 0, 0)
            insets
        }

        changeLayout()

        listOf(binding.ibHome, binding.btnSettings, binding.btnScoreBoard).forEach {
            it.setOnClickListener(this)
        }

        gameViewCell = binding.gameViewCell

        gameViewCell.addCallback(this)
        gameViewCell.initSwipeListener(binding.gameViewCell)


    }

    fun isTutorial(): Boolean {
        if (!sharedPref.isTutorialPlayed || isTutorialFromMainScreen) {
            sharedPref.isTutorialPlayed = true
            return true
        }
        return false
    }

    fun isSoundPlayed():Boolean{
        return  !sharedPref.isMuteSound
    }

    private fun changeLayout() {
        val params = binding.gameLinerLayout.layoutParams
        params.width = Resources.getSystem().displayMetrics.widthPixels
        params.height = params.width
        val difference = boardCols.toDouble() / boardRows

        when {
            boardRows == Boards.BOARD_3x3.rows && boardCols == Boards.BOARD_3x3.cols -> {
                params.width = (params.width * 0.8).toInt()
                params.height = params.width
            }

            boardRows == Boards.BOARD_4x4.rows && boardCols == Boards.BOARD_4x4.cols -> {
                params.width = (params.width * 0.85).toInt()
                params.height = params.width
            }

            boardRows != boardCols -> {
                params.width = (params.width * 1.1 * difference).toInt()
                params.height = (params.height * 1.1).toInt()
            }
        }

        binding.gameLinerLayout.layoutParams = params
    }

    fun updateScore(score: Long, topScore: Long) {
        runOnUiThread {
            if (score == 0L) {
                binding.currentScoreTextview.text = getString(R.string.start)
            } else {
                binding.currentScoreTextview.text = score.toString()
            }
            binding.bestScoreTextview.text = topScore.toString()
        }
    }

    override fun onClick(v: View?) {

        when (v) {
            binding.ibHome -> {
                startActivity(Intent(this@GameActivity, HomeActivity::class.java))
                finish()
            }

            binding.btnSettings -> {

            }

            binding.btnScoreBoard -> {

            }
        }
    }


    private lateinit var thread: ThreadMain

    override fun surfaceCreated(holder: SurfaceHolder) {
        thread = ThreadMain(holder, binding.gameViewCell)
        thread.start()
    }

    override fun surfaceChanged(holder: SurfaceHolder) {
        thread.setSurfaceHolder(holder)
    }


    override fun surfaceDestroyed() {
//        thread.setRunning()
    }

    fun setThread(thread: ThreadMain) {
        this.thread = thread
    }

    fun destroyGameThread() {
        surfaceDestroyed()
    }


}