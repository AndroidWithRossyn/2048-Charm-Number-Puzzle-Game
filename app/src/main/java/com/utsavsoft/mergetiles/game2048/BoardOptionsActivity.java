package com.utsavsoft.mergetiles.game2048;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
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

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.utsavsoft.mergetiles.game2048.maingamecode.BoardType;
import com.utsavsoft.mergetiles.game2048.services.HomeWatcher;
import com.utsavsoft.mergetiles.game2048.services.Music;
import com.utsavsoft.mergetiles.game2048.interfaces.OnHomePressedListener;
import com.utsavsoft.mergetiles.game2048.util.Utils;

import java.util.ArrayList;

public class BoardOptionsActivity extends AppCompatActivity {

    private int boardsIndex, modesIndex, exponent;
    private boolean secondViewIsVisible, thirdViewIsVisible;

    private ArrayList<BoardType> currentDisplayedBoards;
    private ArrayList<BoardType> squareBoards = new ArrayList<>();
    private ArrayList<BoardType> rectangleBoards = new ArrayList<>();

    private RelativeLayout layoutFirst, layoutSecond, layoutThird;
    private Animation rightAnimIn, leftAnimIn, rightAnimOut, leftAnimOut;

    HomeWatcher mHomeWatcher;

    private Utils utils;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.board_options_activity);
        hideSystemUI();

        utils = new Utils(BoardOptionsActivity.this);
        if (!utils.getBooleanValue(Utils.mute_music)) {
            playMusic();
        }

        boardsIndex = 0;
        modesIndex = 0;
        exponent = 2;
        secondViewIsVisible = false;
        thirdViewIsVisible = false;

        setModeSelecting();
        initBoardsArrays();
        setBoardSelecting();
        setExponentSelecting();

        layoutFirst = findViewById(R.id.select_layout_frist);
        layoutSecond = findViewById(R.id.select_layout_second);
        layoutThird = findViewById(R.id.select_layout_third);

        Animation scaleAnimation = AnimationUtils.loadAnimation(BoardOptionsActivity.this, R.anim.scale_animation);

        rightAnimIn = AnimationUtils.loadAnimation(BoardOptionsActivity.this, R.anim.slide_in_right_side);
        leftAnimIn = AnimationUtils.loadAnimation(BoardOptionsActivity.this, R.anim.slide_in_left_side);
        rightAnimOut = AnimationUtils.loadAnimation(BoardOptionsActivity.this, R.anim.slide_out_right_side);
        leftAnimOut = AnimationUtils.loadAnimation(BoardOptionsActivity.this, R.anim.slide_out_left_side);

        Button btnNextFirst = findViewById(R.id.button_next);
        btnNextFirst.setAnimation(scaleAnimation);
        btnNextFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

            }
        });

        Button btnNextSecond = findViewById(R.id.button_next_choose_bord);
        btnNextSecond.setAnimation(scaleAnimation);
        btnNextSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

            }
        });

        Button btnPlay = findViewById(R.id.button_play);
        btnPlay.setAnimation(scaleAnimation);
        setExponentSelecting();
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playClick();
                Intent intent = new Intent(BoardOptionsActivity.this, GameActivity.class);
                intent.putExtra("rows", currentDisplayedBoards.get(boardsIndex).rows);
                intent.putExtra("cols", currentDisplayedBoards.get(boardsIndex).cols);
                intent.putExtra("exponent", exponent);
                intent.putExtra("game_mode", modesIndex);
                startActivity(intent);
            }
        });

    }


    private void initBoardsArrays() {
        BoardType boardType;


        boardType = new BoardType(4, 4, getDrawable(R.drawable.board_4x4));
        squareBoards.add(boardType);
        boardType = new BoardType(5, 5, getDrawable(R.drawable.board_5x5));
        squareBoards.add(boardType);
        boardType = new BoardType(6, 6, getDrawable(R.drawable.board_6x6));
        squareBoards.add(boardType);
        boardType = new BoardType(8, 8, getDrawable(R.drawable.board_8x8));
        squareBoards.add(boardType);
        boardType = new BoardType(3, 3, getDrawable(R.drawable.board_3x3));
        squareBoards.add(boardType);

        boardType = new BoardType(4, 3, getDrawable(R.drawable.board_3x4));
        rectangleBoards.add(boardType);
        boardType = new BoardType(5, 3, getDrawable(R.drawable.board_3x5));
        rectangleBoards.add(boardType);
        boardType = new BoardType(5, 4, getDrawable(R.drawable.board_4x5));
        rectangleBoards.add(boardType);
        boardType = new BoardType(6, 5, getDrawable(R.drawable.board_5x6));
        rectangleBoards.add(boardType);

    }


    private void setModeSelecting() {
        final ArrayList<Drawable> modeTypes = new ArrayList<>();
        final ArrayList<String> modeNames = new ArrayList<>();
        final ArrayList<String> modeExp = new ArrayList<>();

        modeTypes.add(getDrawable(R.drawable.classic_mode));
        modeNames.add(getString(R.string.mode_classic));
        modeExp.add(getString(R.string.mode_exp_classic));
        modeTypes.add(getDrawable(R.drawable.block_mode));
        modeNames.add(getString(R.string.mode_blocks));
        modeExp.add(getString(R.string.mode_exp_blocks));
        modeTypes.add(getDrawable(R.drawable.anim_mode_suffile));
        modeNames.add(getString(R.string.mode_shuffle));
        modeExp.add(getString(R.string.mode_exp_shuffle));

        final TextView tvModeExp = findViewById(R.id.textview_mode_exp);
        final TextView tvModeName = findViewById(R.id.textview_mode_type);
        final ImageView ivMode = findViewById(R.id.imageview_select_mode);
        ivMode.setImageDrawable(modeTypes.get(0));

        final ImageButton ibBtnLeft = findViewById(R.id.left_btn);
        ImageButton ibBtnRight = findViewById(R.id.right_btn);
        AnimationDrawable animationDrawable = (AnimationDrawable) modeTypes.get(2);
        animationDrawable.start();

        ibBtnLeft.setOnClickListener(v -> {
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
                    ivMode.setImageDrawable(modeTypes.get(modesIndex));
                    tvModeName.setText(modeNames.get(modesIndex));
                    tvModeExp.setText(modeExp.get(modesIndex));
                    ivMode.startAnimation(leftAnimIn);
                }
            });
            ivMode.startAnimation(rightAnimOut);
        });
        ibBtnRight.setOnClickListener(v -> {
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
                    ivMode.setImageDrawable(modeTypes.get(modesIndex));
                    tvModeName.setText(modeNames.get(modesIndex));
                    tvModeExp.setText(modeExp.get(modesIndex));
                    ivMode.startAnimation(rightAnimIn);
                }
            });
            ivMode.startAnimation(leftAnimOut);

        });


    }

    private void setExponentSelecting() {
        final ArrayList<String> exponentExpText = new ArrayList<>();
        exponentExpText.add(getString(R.string.exponent_exp_2));
        exponentExpText.add(getString(R.string.exponent_exp_3));
        exponentExpText.add(getString(R.string.exponent_exp_4));
        exponentExpText.add(getString(R.string.exponent_exp_5));
        Animation btnScaleAnim = AnimationUtils.loadAnimation(BoardOptionsActivity.this, R.anim.scale_animation);
        final RadioGroup rgExponentTop = findViewById(R.id.rg_exponent_top);
        final RadioGroup rgExponentBottom = findViewById(R.id.rg_exponent_bottom);
        final RadioButton radioButton2 = findViewById(R.id.rb_exponent_2);
        radioButton2.setAnimation(btnScaleAnim);
        final RadioButton radioButton3 = findViewById(R.id.rb_exponent_3);
        radioButton3.setAnimation(btnScaleAnim);
        final RadioButton radioButton4 = findViewById(R.id.rb_exponent_4);
        radioButton4.setAnimation(btnScaleAnim);
        final RadioButton radioButton5 = findViewById(R.id.rb_exponent_5);
        radioButton5.setAnimation(btnScaleAnim);
        final TextView textViewExponentExp = findViewById(R.id.tv_exponent_exp);

        radioButton2.setOnClickListener(v -> {
            playClick();
            radioButton2.setChecked(true);
            rgExponentBottom.clearCheck();
            exponent = 2;
            textViewExponentExp.setText(exponentExpText.get(0));

        });
        radioButton3.setOnClickListener(v -> {
            playClick();
            radioButton3.setChecked(true);
            rgExponentBottom.clearCheck();
            exponent = 3;
            textViewExponentExp.setText(exponentExpText.get(1));

        });
        radioButton4.setOnClickListener(v -> {
            playClick();
            radioButton4.setChecked(true);
            rgExponentTop.clearCheck();
            exponent = 4;
            textViewExponentExp.setText(exponentExpText.get(2));

        });
        radioButton5.setOnClickListener(v -> {
            playClick();
            radioButton5.setChecked(true);
            rgExponentTop.clearCheck();
            exponent = 5;
            textViewExponentExp.setText(exponentExpText.get(3));
        });

    }

    private void setBoardSelecting() {

        currentDisplayedBoards = squareBoards;
        final TextView tvBoardType = findViewById(R.id.tv_game_size);
        final ImageView ivBoardType = findViewById(R.id.choose_game_image);
        ivBoardType.setImageDrawable(currentDisplayedBoards.get(0).drawable);
        tvBoardType.setText(currentDisplayedBoards.get(0).getTypeString());
        final ImageButton btnLeft = findViewById(R.id.button_left_board_type);
        ImageButton btnRight = findViewById(R.id.button_right_board_type);


        final RadioGroup shapeRadioGroup = findViewById(R.id.radiogroup_board_shape);
        final RadioButton radioButtonRectangle = findViewById(R.id.radio_button_rectangle);
        final RadioButton radioButtonSquare = findViewById(R.id.radio_button_square);
        radioButtonSquare.setTextColor(Color.rgb(90, 85, 83));
        radioButtonRectangle.setTextColor(Color.rgb(167, 168, 168));


        shapeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            playClick();
            if (checkedId == radioButtonRectangle.getId()) {
                boardsIndex = 0;
                currentDisplayedBoards = rectangleBoards;
                tvBoardType.setText(currentDisplayedBoards.get(boardsIndex).getTypeString());
                ivBoardType.setImageDrawable(currentDisplayedBoards.get(boardsIndex).drawable);
                radioButtonRectangle.setTextColor(Color.rgb(90, 85, 83));
                radioButtonSquare.setTextColor(Color.rgb(167, 168, 168));

            } else if (checkedId == radioButtonSquare.getId()) {
                boardsIndex = 0;
                currentDisplayedBoards = squareBoards;
                tvBoardType.setText(currentDisplayedBoards.get(boardsIndex).getTypeString());
                ivBoardType.setImageDrawable(currentDisplayedBoards.get(boardsIndex).drawable);
                radioButtonSquare.setTextColor(Color.rgb(90, 85, 83));
                radioButtonRectangle.setTextColor(Color.rgb(167, 168, 168));
            }
        });


        btnLeft.setOnClickListener(v -> {
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
                    ivBoardType.setImageDrawable(currentDisplayedBoards.get(boardsIndex).drawable);
                    ivBoardType.startAnimation(leftAnimIn);
                    tvBoardType.setText(currentDisplayedBoards.get(boardsIndex).getTypeString());
                }
            });
            ivBoardType.startAnimation(rightAnimOut);

        });
        btnRight.setOnClickListener(v -> {
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
                    ivBoardType.setImageDrawable(currentDisplayedBoards.get(boardsIndex).drawable);
                    ivBoardType.startAnimation(rightAnimIn);
                    tvBoardType.setText(currentDisplayedBoards.get(boardsIndex).getTypeString());
                }
            });
            ivBoardType.startAnimation(leftAnimOut);


        });


    }

    private boolean mIsBound = false;
    private Music mServ;
    private ServiceConnection Scon = new ServiceConnection() {

        public void onServiceConnected(ComponentName name, IBinder
                binder) {
            mServ = ((Music.ServiceBinder) binder).getService();
        }

        public void onServiceDisconnected(ComponentName name) {
            mServ = null;
        }
    };

    void doBindService() {
        bindService(new Intent(this, Music.class),
                Scon, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void doUnbindService() {
        if (mIsBound) {
            unbindService(Scon);
            mIsBound = false;
        }
    }

    private void playClick() {
        final MediaPlayer click = MediaPlayer.create(BoardOptionsActivity.this, R.raw.click);
        if (!utils.getBooleanValue(Utils.mute_sounds)) {
            click.start();
        }
    }

    private void playMusic() {

        doBindService();
        Intent music = new Intent();
        music.setClass(this, Music.class);
        startService(music);


        mHomeWatcher = new HomeWatcher(this);
        mHomeWatcher.setOnHomePressedListener(new OnHomePressedListener() {
            @Override
            public void onHomePressed() {
                if (mServ != null) {
                    mServ.pauseMusic();
                }
            }

            @Override
            public void onHomeLongPressed() {
                if (mServ != null) {
                    mServ.pauseMusic();
                }
            }
        });
        mHomeWatcher.startWatch();

    }

    @Override
    public void onBackPressed() {
        playClick();
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
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mServ != null) {
            mServ.resumeMusic();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        PowerManager pm = (PowerManager)
                getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = false;
        if (pm != null) {
            isScreenOn = pm.isInteractive();
        }

        if (!isScreenOn) {
            if (mServ != null) {
                mServ.pauseMusic();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        doUnbindService();
        Intent music = new Intent();
        music.setClass(this, Music.class);
        stopService(music);

    }


    private void hideSystemUI() {

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        View mContentView = getWindow().getDecorView();

        if (Build.VERSION.SDK_INT >= 30) {
            mContentView.getWindowInsetsController().hide(
                    android.view.WindowInsets.Type.statusBars() | android.view.WindowInsets.Type.navigationBars());
        } else {

            mContentView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LOW_PROFILE
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            );
        }


    }
}




