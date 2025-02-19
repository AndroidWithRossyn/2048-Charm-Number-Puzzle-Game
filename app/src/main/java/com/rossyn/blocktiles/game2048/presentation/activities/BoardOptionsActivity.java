package com.rossyn.blocktiles.game2048.presentation.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.rossyn.blocktiles.game2048.R;
import com.rossyn.blocktiles.game2048.databinding.BoardOptionsActivityBinding;
import com.rossyn.blocktiles.game2048.domain.models.BoardType;
import com.rossyn.blocktiles.game2048.presentation.utils.AnimExt;
import com.rossyn.blocktiles.game2048.services.Music;
import com.rossyn.blocktiles.game2048.presentation.interfaces.OnHomePressedListener;
import com.rossyn.blocktiles.game2048.data.prefs.SharedPref;

import java.util.ArrayList;

public class BoardOptionsActivity extends BaseActivity {

    private int boardsIndex, modesIndex, exponent;
    private boolean secondViewIsVisible, thirdViewIsVisible;

    private ArrayList<BoardType> currentDisplayedBoards;
    private final ArrayList<BoardType> squareBoards = new ArrayList<>();
    private final ArrayList<BoardType> rectangleBoards = new ArrayList<>();

    private RelativeLayout layoutFirst, layoutSecond, layoutThird;
    private Animation rightAnimIn, leftAnimIn, rightAnimOut, leftAnimOut;

    @Override
    protected void onBackPressedCustom() {
        if (thirdViewIsVisible) {
            rightAnimOut.setAnimationListener(new Animation.AnimationListener() {
                public void onAnimationStart(Animation animation) {
                }

                public void onAnimationRepeat(Animation animation) {
                }

                public void onAnimationEnd(Animation animation) {
                    layoutThird.setVisibility(View.GONE);
                    layoutSecond.setVisibility(View.VISIBLE);
                    layoutSecond.startAnimation(leftAnimIn);
                    thirdViewIsVisible = false;
                }
            });
            layoutThird.startAnimation(rightAnimOut);
        } else if (secondViewIsVisible) {
            rightAnimOut.setAnimationListener(new Animation.AnimationListener() {
                public void onAnimationStart(Animation animation) {
                }

                public void onAnimationRepeat(Animation animation) {
                }

                public void onAnimationEnd(Animation animation) {
                    layoutSecond.setVisibility(View.GONE);
                    layoutFirst.setVisibility(View.VISIBLE);
                    layoutFirst.startAnimation(leftAnimIn);
                    secondViewIsVisible = false;
                }
            });
            layoutSecond.startAnimation(rightAnimOut);
        } else {
            finish();
        }
    }

    private BoardOptionsActivityBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = BoardOptionsActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, windowInsets) -> {
//            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemGestures());
            v.setPadding(0, 0, 0, 0);
            return windowInsets;
        });

        boardsIndex = 0;
        modesIndex = 0;
        exponent = 2;
        secondViewIsVisible = false;
        thirdViewIsVisible = false;

        setModeSelecting();
        initBoardsArrays();
        setBoardSelecting();
        setExponentSelecting();

        layoutFirst = binding.selectLayoutFrist;
        layoutSecond = binding.selectLayoutSecond;
        layoutThird = binding.selectLayoutThird;



        rightAnimIn = AnimationUtils.loadAnimation(BoardOptionsActivity.this, R.anim.slide_in_right_side);
        leftAnimIn = AnimationUtils.loadAnimation(BoardOptionsActivity.this, R.anim.slide_in_left_side);
        rightAnimOut = AnimationUtils.loadAnimation(BoardOptionsActivity.this, R.anim.slide_out_right_side);
        leftAnimOut = AnimationUtils.loadAnimation(BoardOptionsActivity.this, R.anim.slide_out_left_side);


        binding.buttonNext.setAnimation(scaleAnim);
        binding.buttonNext.setOnClickListener(v -> {
            playClick();
            leftAnimOut.setAnimationListener(new Animation.AnimationListener() {
                public void onAnimationStart(Animation animation) {
                }

                public void onAnimationRepeat(Animation animation) {
                }

                public void onAnimationEnd(Animation animation) {
                    layoutFirst.setVisibility(View.GONE);
                    layoutSecond.startAnimation(rightAnimIn);
                    layoutSecond.setVisibility(View.VISIBLE);
                    secondViewIsVisible = true;

                }
            });
            layoutFirst.startAnimation(leftAnimOut);

        });

        binding.buttonNextChooseBord.setAnimation(scaleAnim);
        binding.buttonNextChooseBord.setOnClickListener(v -> {
            playClick();
            leftAnimOut.setAnimationListener(new Animation.AnimationListener() {
                public void onAnimationStart(Animation animation) {
                }

                public void onAnimationRepeat(Animation animation) {
                }

                public void onAnimationEnd(Animation animation) {
                    layoutSecond.setVisibility(View.GONE);
                    layoutThird.startAnimation(rightAnimIn);
                    layoutThird.setVisibility(View.VISIBLE);
                    thirdViewIsVisible = true;
                }
            });
            layoutSecond.startAnimation(leftAnimOut);

        });


        binding.buttonPlay.setAnimation(scaleAnim);
        binding.buttonPlay.setOnClickListener(v -> {
            playClick();
            Intent intent = new Intent(BoardOptionsActivity.this, GameActivity.class);
            intent.putExtra("rows", currentDisplayedBoards.get(boardsIndex).rows);
            intent.putExtra("cols", currentDisplayedBoards.get(boardsIndex).cols);
            intent.putExtra("exponent", exponent);
            intent.putExtra("game_mode", modesIndex);
            startActivity(intent);
        });

    }


    private void initBoardsArrays() {
        squareBoards.add(new BoardType(4, 4, ContextCompat.getDrawable(this, R.drawable.board_4x4)));
        squareBoards.add(new BoardType(5, 5, ContextCompat.getDrawable(this, R.drawable.board_5x5)));
        squareBoards.add(new BoardType(6, 6, ContextCompat.getDrawable(this, R.drawable.board_6x6)));
        squareBoards.add(new BoardType(8, 8, ContextCompat.getDrawable(this, R.drawable.board_8x8)));
        squareBoards.add(new BoardType(3, 3, ContextCompat.getDrawable(this, R.drawable.board_3x3)));

        rectangleBoards.add(new BoardType(4, 3, ContextCompat.getDrawable(this, R.drawable.board_3x4)));
        rectangleBoards.add(new BoardType(5, 3, ContextCompat.getDrawable(this, R.drawable.board_3x5)));
        rectangleBoards.add(new BoardType(5, 4, ContextCompat.getDrawable(this, R.drawable.board_4x5)));
        rectangleBoards.add(new BoardType(6, 5, ContextCompat.getDrawable(this, R.drawable.board_5x6)));
    }


    private void setModeSelecting() {
        final ArrayList<Drawable> modeTypes = new ArrayList<>();
        final ArrayList<String> modeNames = new ArrayList<>();
        final ArrayList<String> modeExp = new ArrayList<>();

        modeTypes.add(ContextCompat.getDrawable(this, R.drawable.classic_mode));
        modeNames.add(getString(R.string.mode_classic));
        modeExp.add(getString(R.string.mode_exp_classic));

        modeTypes.add(ContextCompat.getDrawable(this, R.drawable.block_mode));
        modeNames.add(getString(R.string.mode_blocks));
        modeExp.add(getString(R.string.mode_exp_blocks));
        modeTypes.add(ContextCompat.getDrawable(this, R.drawable.anim_mode_suffile));
        modeNames.add(getString(R.string.mode_shuffle));
        modeExp.add(getString(R.string.mode_exp_shuffle));


        binding.imageviewSelectMode.setImageDrawable(modeTypes.get(0));


        AnimationDrawable animationDrawable = (AnimationDrawable) modeTypes.get(2);
        animationDrawable.start();

        binding.leftBtn.setOnClickListener(v -> {
            playClick();
            if (modesIndex == 0) {
                modesIndex = 2;
            } else {
                modesIndex--;
            }
            rightAnimOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    binding.imageviewSelectMode.setImageDrawable(modeTypes.get(modesIndex));
                    binding.textviewModeType.setText(modeNames.get(modesIndex));
                    binding.textviewModeExp.setText(modeExp.get(modesIndex));
                    binding.imageviewSelectMode.startAnimation(leftAnimIn);
                }
            });
            binding.imageviewSelectMode.startAnimation(rightAnimOut);
        });
        binding.rightBtn.setOnClickListener(v -> {
            playClick();
            if (modesIndex == 2) {
                modesIndex = 0;
            } else {
                modesIndex++;
            }
            leftAnimOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    binding.imageviewSelectMode.setImageDrawable(modeTypes.get(modesIndex));
                    binding.textviewModeType.setText(modeNames.get(modesIndex));
                    binding.textviewModeExp.setText(modeExp.get(modesIndex));
                    binding.imageviewSelectMode.startAnimation(rightAnimIn);
                }
            });
            binding.imageviewSelectMode.startAnimation(leftAnimOut);

        });


    }

    private void setExponentSelecting() {
        final ArrayList<String> exponentExpText = new ArrayList<>();
        exponentExpText.add(getString(R.string.exponent_exp_2));
        exponentExpText.add(getString(R.string.exponent_exp_3));
        exponentExpText.add(getString(R.string.exponent_exp_4));
        exponentExpText.add(getString(R.string.exponent_exp_5));
        Animation btnScaleAnim = AnimationUtils.loadAnimation(BoardOptionsActivity.this, R.anim.scale_animation);


        binding.rbExponent2.setAnimation(btnScaleAnim);
        binding.rbExponent3.setAnimation(btnScaleAnim);
        binding.rbExponent4.setAnimation(btnScaleAnim);
        binding.rbExponent5.setAnimation(btnScaleAnim);

        binding.rbExponent2.setOnClickListener(v -> {
            playClick();
            binding.rbExponent2.setChecked(true);
            binding.rgExponentBottom.clearCheck();
            exponent = 2;
            binding.tvExponentExp.setText(exponentExpText.get(0));

        });
        binding.rbExponent3.setOnClickListener(v -> {
            playClick();
            binding.rbExponent3.setChecked(true);
            binding.rgExponentBottom.clearCheck();
            exponent = 3;
            binding.tvExponentExp.setText(exponentExpText.get(1));

        });
        binding.rbExponent4.setOnClickListener(v -> {
            playClick();
            binding.rbExponent4.setChecked(true);
            binding.rgExponentTop.clearCheck();
            exponent = 4;
            binding.tvExponentExp.setText(exponentExpText.get(2));

        });
        binding.rbExponent5.setOnClickListener(v -> {
            playClick();
            binding.rbExponent5.setChecked(true);
            binding.rgExponentTop.clearCheck();
            exponent = 5;
            binding.tvExponentExp.setText(exponentExpText.get(3));
        });

    }

    private void setBoardSelecting() {

        currentDisplayedBoards = squareBoards;
        binding.chooseGameImage.setImageDrawable(currentDisplayedBoards.get(0).drawable);
        binding.tvGameSize.setText(currentDisplayedBoards.get(0).getTypeString());

        binding.radioButtonSquare.setTextColor(Color.rgb(90, 85, 83));
        binding.radioButtonRectangle.setTextColor(Color.rgb(167, 168, 168));


        binding.radiogroupBoardShape.setOnCheckedChangeListener((group, checkedId) -> {
            playClick();
            if (checkedId == binding.radioButtonRectangle.getId()) {
                boardsIndex = 0;
                currentDisplayedBoards = rectangleBoards;
                binding.tvGameSize.setText(currentDisplayedBoards.get(boardsIndex).getTypeString());
                binding.chooseGameImage.setImageDrawable(currentDisplayedBoards.get(boardsIndex).drawable);
                binding.radioButtonRectangle.setTextColor(Color.rgb(90, 85, 83));
                binding.radioButtonSquare.setTextColor(Color.rgb(167, 168, 168));

            } else if (checkedId == binding.radioButtonSquare.getId()) {
                boardsIndex = 0;
                currentDisplayedBoards = squareBoards;
                binding.tvGameSize.setText(currentDisplayedBoards.get(boardsIndex).getTypeString());
                binding.chooseGameImage.setImageDrawable(currentDisplayedBoards.get(boardsIndex).drawable);
                binding.radioButtonSquare.setTextColor(Color.rgb(90, 85, 83));
                binding.radioButtonRectangle.setTextColor(Color.rgb(167, 168, 168));
            }
        });


        binding.buttonLeftBoardType.setOnClickListener(v -> {
            playClick();
            if (boardsIndex == 0) {
                boardsIndex = currentDisplayedBoards.size() - 1;
            } else {
                boardsIndex--;
            }
            rightAnimOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    binding.chooseGameImage.setImageDrawable(currentDisplayedBoards.get(boardsIndex).drawable);
                    binding.chooseGameImage.startAnimation(leftAnimIn);
                    binding.tvGameSize.setText(currentDisplayedBoards.get(boardsIndex).getTypeString());
                }
            });
            binding.chooseGameImage.startAnimation(rightAnimOut);

        });

        binding.buttonRightBoardType.setOnClickListener(v -> {
            playClick();
            if (boardsIndex == currentDisplayedBoards.size() - 1) {
                boardsIndex = 0;
            } else {
                boardsIndex++;
            }
            leftAnimOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    binding.chooseGameImage.setImageDrawable(currentDisplayedBoards.get(boardsIndex).drawable);
                    binding.chooseGameImage.startAnimation(rightAnimIn);
                    binding.tvGameSize.setText(currentDisplayedBoards.get(boardsIndex).getTypeString());
                }
            });
            binding.chooseGameImage.startAnimation(leftAnimOut);


        });


    }
}




