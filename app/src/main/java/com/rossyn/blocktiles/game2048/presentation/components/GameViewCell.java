package com.rossyn.blocktiles.game2048.presentation.components;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.rossyn.blocktiles.game2048.domain.utils.Scores;
import com.rossyn.blocktiles.game2048.domain.utils.ThreadMain;
import com.rossyn.blocktiles.game2048.presentation.activities.GameActivity;
import com.rossyn.blocktiles.game2048.R;
import com.rossyn.blocktiles.game2048.data.prefs.SharedPref;
import com.rossyn.blocktiles.game2048.presentation.interfaces.OnSwipeTouchListener;

import java.util.Objects;


public final class GameViewCell extends SurfaceView implements SurfaceHolder.Callback {

    private final MediaPlayer swipe;
    private ThreadMain thread;
    private boolean isInit, isTutorial, isWinningMsgPlayed, isNewScoreMsgPlayed;
    private final boolean isTutorialFromMainScreen;

    private final Scores scores;
    GameBoardView gameBoardView;
    Boolean dialogOpen = false;
    Drawable backgroundRectangle = getResources().getDrawable(R.drawable.game_background);
    Drawable cellRectangle = getResources().getDrawable(R.drawable.cell_shape);
    AlertDialog.Builder builder;
    GameActivity gameActivity;
    private final Dialog gameOverDialog;


    public GameViewCell(Context context, AttributeSet attrs) {
        super(context, attrs);

        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);


        getHolder().addCallback(this);
        setZOrderOnTop(true);    // necessary
        getHolder().setFormat(PixelFormat.TRANSPARENT);

        this.gameActivity = (GameActivity) context;
        swipe = MediaPlayer.create(gameActivity, R.raw.swipe);

        isInit = false;
        int exponent = gameActivity.getBoardExponent();
        int rows = gameActivity.getBoardRows();
        int cols = gameActivity.getBoardCols();
        int gameMode = gameActivity.getGameMode();

        isTutorial = gameActivity.isTutorial();
        isTutorialFromMainScreen = gameActivity.isTutorialFromMainScreen();
        this.scores = new Scores((long) 0, getContext().getSharedPreferences(SharedPref.SHARED_PREF_FILE_NAME, Context.MODE_PRIVATE), gameMode, rows, cols);

        gameBoardView = new GameBoardView(rows, cols, exponent, this, gameMode);
        BitmapCreator.exponent = exponent;

        builder = new AlertDialog.Builder(GameActivity.getContext());
        gameOverDialog = new Dialog(context);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread = new ThreadMain(holder, this);
        gameActivity.setThread(thread);
        thread.setRunning(true);
        thread.start();

        gameActivity.updateScore(scores.getScore(), scores.getTopScore());
        initBarButtons();

        prepareGameOverDialog();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        thread.setSurfaceHolder(holder);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        while (retry) {
            try {
                thread.setRunning(false);
                thread.join();
                retry = false;
                BitmapCreator bitmapCreator = new BitmapCreator();
                bitmapCreator.clearBitmapArray();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public void update() {
        if (gameBoardView.isGameOver() && !dialogOpen) {
            dialogOpen = true;
            gameOverDialog();
        }

        if (scores.isNewHighScore()) {
            scores.updateScoreBoard();
            scores.refreshScoreBoard();

            if (!isNewScoreMsgPlayed) {
                showAnnouncingMsg(getResources().getString(R.string.new_score));
                isNewScoreMsgPlayed = true;
            }
        }
        if (!isWinningMsgPlayed && gameBoardView.isGameWon()) {
            showAnnouncingMsg(getResources().getString(R.string.winner));
            isWinningMsgPlayed = true;
        }
        if (isInit) {
            gameBoardView.update();
        }
    }


    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        drawDrawable(canvas, backgroundRectangle, 0, 0, getWidth(), getHeight());
        drawEmptyBoard(canvas);
        if (!isInit) {
            if (isTutorial) {
                firstScreenTutorial();
                gameBoardView.initTutorialBoard();
            } else {
                gameBoardView.initBoard();
            }
        }
        isInit = true;
        gameBoardView.draw(canvas);
    }

    public void updateScore(long value) {
        scores.updateScore(value);
        gameActivity.updateScore(scores.getScore(), scores.getTopScore());

    }

    private void drawEmptyBoard(Canvas canvas) {
        drawDrawable(canvas, backgroundRectangle, 0, 0, getWidth(), getHeight());

        int padding = (int) pxFromDp(3);
        int width = getWidth() - padding * 2;
        int height = getHeight() - padding * 2;

        int cellWidth = width / gameBoardView.getCols();
        int cellHeight = height / gameBoardView.getRows();

        BitmapCreator.cellDefaultHeight = cellHeight;
        BitmapCreator.cellDefaultWidth = cellWidth;


        for (int x = 0; x < gameBoardView.getCols(); x++) {
            for (int y = 0; y < gameBoardView.getRows(); y++) {

                int posX = x * cellWidth + padding;
                int posY = y * cellHeight + padding;

                int posXX = posX + cellWidth;
                int posYY = posY + cellHeight;


                cellRectangle.setColorFilter(getResources().getColor(R.color.valueEmpty), PorterDuff.Mode.SRC_OVER);
                drawDrawable(canvas, cellRectangle, posX, posY, posXX, posYY);

                if (!isInit) {
                    gameBoardView.setPositions(y, x, posX, posY);
                }
            }
        }
    }

    private void drawDrawable(Canvas canvas, Drawable draw, int startingX, int startingY, int endingX, int endingY) {
        draw.setBounds(startingX, startingY, endingX, endingY);
        draw.draw(canvas);
    }

   public void initSwipeListener(View view) {
        view.setOnTouchListener(new OnSwipeTouchListener(view.getContext()) {
            public void onSwipeTop() {
                if (!dialogOpen) {
                    gameBoardView.up();
                    secondScreenTutorial();
                }
            }

            public void onSwipeRight() {
                if (!dialogOpen) {
                    gameBoardView.right();
                    secondScreenTutorial();
                }
            }

            public void onSwipeLeft() {
                if (!dialogOpen) {
                    gameBoardView.left();
                    secondScreenTutorial();
                }
            }

            public void onSwipeBottom() {
                if (!dialogOpen) {
                    gameBoardView.down();
                    secondScreenTutorial();
                }
            }
        });
    }

    private void initBarButtons() {
        ImageButton resetBtn = gameActivity.findViewById(R.id.ib_reset);
        resetBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isTutorial) {
                    playClick();
                    if (scores.isNewHighScore())
                        scores.updateScoreBoard();
                    scores.refreshScoreBoard();
                    gameBoardView.resetGame();
                    scores.resetGame();
                    gameActivity.updateScore(scores.getScore(), scores.getTopScore());
                    isNewScoreMsgPlayed = false;
                    isWinningMsgPlayed = false;
                }

            }
        });
        ImageButton undoBtn = gameActivity.findViewById(R.id.ib_undo);
        undoBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isTutorial) {
                    playClick();
                    gameBoardView.undoMove();
                    scores.undoScore();
                    gameActivity.updateScore(scores.getScore(), scores.getTopScore());
                }
            }
        });
    }

    public void prepareGameOverDialog() {
        gameOverDialog.setContentView(R.layout.dialog_gameover);
        Objects.requireNonNull(gameOverDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        gameOverDialog.setCancelable(false);

        Animation scaleAnim = AnimationUtils.loadAnimation(gameActivity, R.anim.scale_animation);
        Button playAgainBtn = gameOverDialog.findViewById(R.id.btn_play_again);
        playAgainBtn.startAnimation(scaleAnim);
        playAgainBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                playClick();
                gameBoardView.resetGame();
                gameOverDialog.dismiss();
                dialogOpen = false;
            }
        });
    }

    public void gameOverDialog() {
        gameActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final TextView tvScore = gameOverDialog.findViewById(R.id.game_over_score_num);
                tvScore.setText(String.valueOf(scores.getScore()));
                gameOverDialog.show();
            }
        });

    }

    public void ShowShufflingMsg() {
        final ImageView shufflingBackground = gameActivity.findViewById(R.id.background_dark);
        final TextView shufflingText = gameActivity.findViewById(R.id.announcing_msg);

        gameActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialogOpen = true;
                shufflingText.setText(getResources().getString(R.string.shuffle));
                shufflingText.setTextColor(getResources().getColor(R.color.value2));
                shufflingText.setVisibility(VISIBLE);
                shufflingBackground.setVisibility(VISIBLE);
                new CountDownTimer(1000, 100) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                    }

                    @Override
                    public void onFinish() {
                        shufflingText.setVisibility(GONE);
                        shufflingBackground.setVisibility(GONE);
                        dialogOpen = false;
                    }
                }.start();
            }
        });
    }

    public void firstScreenTutorial() {
        final ImageView tutorialBackground = gameActivity.findViewById(R.id.background_dark);
        final TextView tutorialText = gameActivity.findViewById(R.id.tutorial_textview);
        gameActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tutorialBackground.setVisibility(VISIBLE);
                tutorialText.setVisibility(VISIBLE);
                tutorialText.setText(gameActivity.getString(R.string.tutorial_first_line));
            }
        });
    }

    public void secondScreenTutorial() {
        final TextView tutorialText = gameActivity.findViewById(R.id.tutorial_textview);
        gameActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tutorialText.setText(gameActivity.getString(R.string.tutorial_second_line));

            }
        });
    }

    public void thirdScreenTutorial() {
        final ImageView tutorialBackground = gameActivity.findViewById(R.id.background_dark);
        final TextView tutorialText = gameActivity.findViewById(R.id.tutorial_textview);
        final Button endBtn = gameActivity.findViewById(R.id.button_end_tutorial);
        Animation scaleAnim = AnimationUtils.loadAnimation(gameActivity, R.anim.scale_animation);
        endBtn.startAnimation(scaleAnim);
        gameActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tutorialText.setText(gameActivity.getString(R.string.tutorial_third_line));
                endBtn.setVisibility(VISIBLE);
                dialogOpen = true;
                endBtn.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (isTutorialFromMainScreen) {
                            gameActivity.finish();
                        } else {
                            playClick();
                            endBtn.clearAnimation();
                            tutorialBackground.setVisibility(GONE);
                            tutorialText.setVisibility(GONE);
                            endBtn.setVisibility(GONE);
                            gameBoardView.setTutorialFinished();
                            isTutorial = false;
                            dialogOpen = false;
                        }
                    }
                });


            }
        });

    }

    public void showAnnouncingMsg(final String msg) {
        final ImageView msgBackground = gameActivity.findViewById(R.id.background_dark);
        final TextView msgText = gameActivity.findViewById(R.id.announcing_msg);

        gameActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                msgText.setText(msg);
                dialogOpen = true;
                msgText.setVisibility(VISIBLE);
                msgBackground.setVisibility(VISIBLE);
                msgText.setTextSize(60);

                new CountDownTimer(2000, 100) {
                    int count = 0;

                    @Override
                    public void onTick(long millisUntilFinished) {
                        switch (count) {
                            case 0:
                                msgText.setTextColor(getResources().getColor(R.color.value2));
                                count++;
                                break;
                            case 1:
                                msgText.setTextColor(getResources().getColor(R.color.value4));
                                count++;
                                break;
                            case 2:
                                msgText.setTextColor(getResources().getColor(R.color.value8));
                                count++;
                                break;
                            default:
                                msgText.setTextColor(getResources().getColor(R.color.value16));
                                count = 0;
                                break;
                        }
                    }

                    @Override
                    public void onFinish() {
                        msgText.setVisibility(GONE);
                        msgBackground.setVisibility(GONE);
                        dialogOpen = false;
                    }
                }.start();
            }
        });
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    public float pxFromDp(final float dp) {
        return dp * getContext().getResources().getDisplayMetrics().density;
    }

    public void playClick() {
        final MediaPlayer click = MediaPlayer.create(gameActivity, R.raw.click);
        if (gameActivity.isSoundPlayed()) {
            click.start();
        }
    }

    public void playSwipe() {
        if (gameActivity.isSoundPlayed()) {
            swipe.start();
        }
    }
}











