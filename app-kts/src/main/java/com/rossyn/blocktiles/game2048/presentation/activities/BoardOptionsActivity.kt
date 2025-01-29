package com.rossyn.blocktiles.game2048.presentation.activities

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.widget.RelativeLayout
import androidx.core.view.ViewCompat
import com.rossyn.blocktiles.game2048.R
import com.rossyn.blocktiles.game2048.databinding.BoardOptionsActivityBinding
import com.rossyn.blocktiles.game2048.domain.models.BoardType
import com.rossyn.blocktiles.game2048.domain.models.Boards
import com.rossyn.blocktiles.game2048.domain.models.Exponent
import com.rossyn.blocktiles.game2048.domain.models.GameMode
import com.rossyn.blocktiles.game2048.presentation.utils.animLoad
import com.rossyn.blocktiles.game2048.presentation.utils.enableEdgeToEdgeAppTrans
import com.rossyn.blocktiles.game2048.presentation.utils.onEnd
import com.rossyn.blocktiles.game2048.presentation.utils.scaleAnim
import com.rossyn.blocktiles.game2048.utils.getDrawableCompat
import dagger.hilt.android.AndroidEntryPoint
import dev.funkymuse.view.gone
import dev.funkymuse.view.visible

@AndroidEntryPoint
class BoardOptionsActivity : BaseActivity(), View.OnClickListener {

    override fun onBackPressedCustom() {
        if (thirdViewIsVisible) {
            rightAnimOut.onEnd {
                layoutThird.gone()
                layoutSecond.visible()
                layoutSecond.startAnimation(leftAnimIn)
                thirdViewIsVisible = false
            }
            layoutThird.startAnimation(rightAnimOut)
        } else if (secondViewIsVisible) {
            rightAnimOut.onEnd {
                layoutSecond.gone()
                layoutFirst.visible()
                layoutFirst.startAnimation(leftAnimIn)
                secondViewIsVisible = false
            }
            layoutSecond.startAnimation(rightAnimOut)
        } else {
            finish()
        }
    }

    private lateinit var binding: BoardOptionsActivityBinding

    private var boardsIndex = 0
    private var modesIndex = 0
    private var exponent = Exponent.Two.value

    private var secondViewIsVisible = false
    private var thirdViewIsVisible: Boolean = false


    private val squareBoards: List<BoardType> by lazy {
        Boards.getSquareBoards { resId -> getDrawableCompat(resId) }
    }

    private val rectangleBoards: List<BoardType> by lazy {
        Boards.getRectangleBoards { resId -> getDrawableCompat(resId) }
    }
    private var currentDisplayedBoards: List<BoardType> = ArrayList()

    private val gameModes: ArrayList<GameMode> = ArrayList(GameMode.entries)

    private val rightAnimIn: Animation by lazy { animLoad(R.anim.slide_in_right_side) }
    private val leftAnimIn: Animation by lazy { animLoad(R.anim.slide_in_left_side) }
    private val rightAnimOut: Animation by lazy { animLoad(R.anim.slide_out_right_side) }
    private val leftAnimOut: Animation by lazy { animLoad(R.anim.slide_out_left_side) }

    private lateinit var layoutFirst: RelativeLayout
    private lateinit var layoutSecond: RelativeLayout
    private lateinit var layoutThird: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdgeAppTrans()
        binding = BoardOptionsActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            v.setPadding(0, 0, 0, 0)
            insets
        }

        layoutFirst = binding.selectLayoutFrist
        layoutSecond = binding.selectLayoutSecond
        layoutThird = binding.selectLayoutThird

        listOf(
            binding.buttonNext,
            binding.buttonNextChooseBord,
            binding.buttonPlay,
            binding.rbExponent2,
            binding.rbExponent3,
            binding.rbExponent4,
            binding.rbExponent5
        ).forEach {
            it.scaleAnim()
            it.setOnClickListener(this@BoardOptionsActivity)
        }

        listOf(
            binding.leftBtn,
            binding.rightBtn,
            binding.buttonLeftBoardType,
            binding.buttonRightBoardType
        ).forEach {
            it.setOnClickListener(this)
        }
        binding.imageviewSelectMode.setImageDrawable(getDrawableCompat(GameMode.Classic.iconRes))

        setBoardSelecting()

    }

    override fun onClick(v: View?) {
        playClick()
        when (v) {
            binding.buttonNext -> {
                leftAnimOut.onEnd {
                    layoutFirst.gone()
                    layoutSecond.startAnimation(rightAnimIn)
                    layoutSecond.visible()
                    secondViewIsVisible = true
                }
                layoutFirst.startAnimation(leftAnimOut)
            }

            binding.buttonNextChooseBord -> {
                leftAnimOut.onEnd {
                    layoutSecond.gone()
                    layoutThird.startAnimation(rightAnimIn)
                    layoutThird.visible()
                    thirdViewIsVisible = true
                }
                layoutSecond.startAnimation(leftAnimOut)
            }

            binding.buttonPlay -> {
                val intent = Intent(this, GameActivity::class.java)
                intent.apply {
                    putExtra("rows", currentDisplayedBoards[boardsIndex].rows)
                    putExtra("cols", currentDisplayedBoards[boardsIndex].cols)
                    putExtra("exponent", exponent)
                    putExtra("game_mode", modesIndex)
                }
                startActivity(intent)
            }

            binding.leftBtn -> {
                if (modesIndex == 0) {
                    modesIndex = (gameModes.size - 1)
                } else {
                    modesIndex--
                }
                rightAnimOut.onEnd {
                    gameModes.getOrNull(modesIndex)?.let {
                        val drawable = getDrawableCompat(it.iconRes)
                        binding.imageviewSelectMode.setImageDrawable(drawable)
                        if (modesIndex == (gameModes.size - 1)) {
                            val animationDrawable = drawable as? AnimationDrawable
                            animationDrawable?.start()
                        }
                        binding.textviewModeType.text = resources.getString(it.typeName)
                        binding.textviewModeExp.text = resources.getString(it.typeDis)
                        binding.imageviewSelectMode.startAnimation(leftAnimIn)
                    }
                }

                binding.imageviewSelectMode.startAnimation(rightAnimOut)


            }

            binding.rightBtn -> {
                if (modesIndex == (gameModes.size - 1)) {
                    modesIndex = 0
                } else {
                    modesIndex++
                }
                leftAnimOut.onEnd {
                    gameModes.getOrNull(modesIndex)?.let {
                        val drawable = getDrawableCompat(it.iconRes)
                        binding.imageviewSelectMode.setImageDrawable(drawable)
                        if (modesIndex == (gameModes.size - 1)) {
                            val animationDrawable = drawable as? AnimationDrawable
                            animationDrawable?.start()
                        }
                        binding.textviewModeType.text = resources.getString(it.typeName)
                        binding.textviewModeExp.text = resources.getString(it.typeDis)
                        binding.imageviewSelectMode.startAnimation(rightAnimIn)
                    }
                }
                binding.imageviewSelectMode.startAnimation(leftAnimOut)
            }

            binding.buttonLeftBoardType -> {
                if (boardsIndex == 0) {
                    boardsIndex = (currentDisplayedBoards.size - 1)
                } else {
                    boardsIndex--
                }

                rightAnimOut.onEnd {
                    currentDisplayedBoards.getOrNull(boardsIndex)?.let {
                        binding.tvGameSize.text = it.getTypeString()
                        binding.chooseGameImage.setImageDrawable(it.drawable)
                        binding.chooseGameImage.startAnimation(leftAnimIn)

                    }
                }
                binding.chooseGameImage.startAnimation(rightAnimOut)
            }

            binding.buttonRightBoardType -> {
                if (boardsIndex == (currentDisplayedBoards.size - 1)) {
                    boardsIndex = 0
                } else {
                    boardsIndex++
                }

                leftAnimOut.onEnd {
                    currentDisplayedBoards.getOrNull(boardsIndex)?.let {
                        binding.tvGameSize.text = it.getTypeString()
                        binding.chooseGameImage.setImageDrawable(it.drawable)
                        binding.chooseGameImage.startAnimation(rightAnimIn)
                    }
                }
                binding.chooseGameImage.startAnimation(leftAnimOut)
            }

            binding.rbExponent2 -> {
                binding.rbExponent2.isChecked = true
                binding.rgExponentBottom.clearCheck()
                exponent = Exponent.Two.value
                binding.tvExponentExp.text = resources.getString(R.string.exponent_exp_2)
            }

            binding.rbExponent3 -> {
                binding.rbExponent3.isChecked = true
                binding.rgExponentBottom.clearCheck()
                exponent = Exponent.Three.value
                binding.tvExponentExp.text = resources.getString(R.string.exponent_exp_3)

            }

            binding.rbExponent4 -> {
                binding.rbExponent4.isChecked = true
                binding.rgExponentTop.clearCheck()
                exponent = Exponent.Four.value
                binding.tvExponentExp.text = resources.getString(R.string.exponent_exp_4)

            }

            binding.rbExponent5 -> {
                binding.rbExponent5.isChecked = true
                binding.rgExponentTop.clearCheck()
                exponent = Exponent.Five.value
                binding.tvExponentExp.text = resources.getString(R.string.exponent_exp_5)

            }
        }
    }

    private fun setBoardSelecting() {
        currentDisplayedBoards = squareBoards
        binding.tvGameSize.text = currentDisplayedBoards.first().getTypeString()
        binding.chooseGameImage.setImageDrawable(currentDisplayedBoards.first().drawable)

        binding.radiogroupBoardShape.setOnCheckedChangeListener { group, checkedId ->
            playClick()
            when (checkedId) {
                binding.radioButtonRectangle.id -> {
                    boardsIndex = 0
                    currentDisplayedBoards = rectangleBoards
                    binding.tvGameSize.text = currentDisplayedBoards.first().getTypeString()
                    binding.chooseGameImage.setImageDrawable(currentDisplayedBoards.first().drawable)
                }

                binding.radioButtonSquare.id -> {
                    boardsIndex = 0
                    currentDisplayedBoards = squareBoards
                    binding.tvGameSize.text = currentDisplayedBoards.first().getTypeString()
                    binding.chooseGameImage.setImageDrawable(currentDisplayedBoards.first().drawable)
                }
            }
        }
    }
}