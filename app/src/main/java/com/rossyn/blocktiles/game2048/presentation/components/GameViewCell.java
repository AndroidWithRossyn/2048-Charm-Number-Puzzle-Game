package com.rossyn.blocktiles.game2048.presentation.components;


import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.rossyn.blocktiles.game2048.domain.utils.Scores;
import com.rossyn.blocktiles.game2048.presentation.activities.GameActivity;
import com.rossyn.blocktiles.game2048.R;
import com.rossyn.blocktiles.game2048.data.prefs.SharedPref;
import com.rossyn.blocktiles.game2048.presentation.interfaces.GameListener;
import com.rossyn.blocktiles.game2048.presentation.interfaces.OnSwipeTouchListener;


public final class GameViewCell extends SurfaceView implements SurfaceHolder.Callback {

    private boolean isInit, isTutorial, isWinningMsgPlayed, isNewScoreMsgPlayed;

    private final Scores scores;
    GameBoardView gameBoardView;
    public Boolean dialogOpen = false;
    Drawable backgroundRectangle;
    Drawable cellRectangle;
    GameActivity gameActivity;

    private GameListener listener;

    public void setOnCustomEventListener(GameListener listener) {
        this.listener = listener;
    }

    public GameViewCell(Context context, AttributeSet attrs) {
        super(context, attrs);
        backgroundRectangle = ContextCompat.getDrawable(this.getContext(), R.drawable.game_background);
        cellRectangle = ContextCompat.getDrawable(this.getContext(), R.drawable.cell_shape);

        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);


        getHolder().addCallback(this);
        setZOrderOnTop(true);    // necessary
        getHolder().setFormat(PixelFormat.TRANSPARENT);

        this.gameActivity = (GameActivity) context;


        isInit = false;
        int exponent = gameActivity.getBoardExponent();
        int rows = gameActivity.getBoardRows();
        int cols = gameActivity.getBoardCols();
        int gameMode = gameActivity.getGameMode();

        isTutorial = gameActivity.isTutorial();

        this.scores = new Scores((long) 0, getContext().getSharedPreferences(SharedPref.SHARED_PREF_FILE_NAME, Context.MODE_PRIVATE), gameMode, rows, cols);

        gameBoardView = new GameBoardView(rows, cols, exponent, this, gameMode);
        BitmapCreator.exponent = exponent;

    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        listener.surfaceCreated(holder);
        listener.updateScore(scores.getScore(), scores.getTopScore());
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        listener.surfaceChanged(holder, format, width, height);
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        listener.surfaceDestroyed(holder);
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
                listener.showAnnouncingMsg(getResources().getString(R.string.new_score));
                isNewScoreMsgPlayed = true;
            }
        }
        if (!isWinningMsgPlayed && gameBoardView.isGameWon()) {
            listener.showAnnouncingMsg(getResources().getString(R.string.winner));
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
        listener.updateScore(scores.getScore(), scores.getTopScore());
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
                    listener.secondScreenTutorial();
                }
            }

            public void onSwipeRight() {
                if (!dialogOpen) {
                    gameBoardView.right();
                    listener.secondScreenTutorial();
                }
            }

            public void onSwipeLeft() {
                if (!dialogOpen) {
                    gameBoardView.left();
                    listener.secondScreenTutorial();
                }
            }

            public void onSwipeBottom() {
                if (!dialogOpen) {
                    gameBoardView.down();
                    listener.secondScreenTutorial();
                }
            }
        });
    }


    public void resetGame() {
        if (!isTutorial) {
            if (scores.isNewHighScore())
                scores.updateScoreBoard();
            scores.refreshScoreBoard();
            gameBoardView.resetGame();
            scores.resetGame();
            listener.updateScore(scores.getScore(), scores.getTopScore());
            isNewScoreMsgPlayed = false;
            isWinningMsgPlayed = false;
        }
    }

    public void undoGame() {
        if (!isTutorial) {
            gameBoardView.undoMove();
            scores.undoScore();
            listener.updateScore(scores.getScore(), scores.getTopScore());
        }
    }


    public void gameOver() {
        gameBoardView.resetGame();
        dialogOpen = false;
    }

    public void gameOverDialog() {
      listener.showGameOver(String.valueOf(scores.getScore()));
    }

    public void ShowShufflingMsg() {
        listener.ShowShufflingMsg();
    }

    public void firstScreenTutorial() {
        listener.firstScreenTutorial();
    }


    public void thirdScreenTutorial() {
        listener.thirdScreenTutorial();
    }

    public void informFinish(){
        gameBoardView.setTutorialFinished();
        isTutorial = false;
        dialogOpen = false;
    }


    @Override
    public boolean performClick() {
        return super.performClick();
    }

    public float pxFromDp(final float dp) {
        return dp * getContext().getResources().getDisplayMetrics().density;
    }


    public void playSwipe() {
        listener.playSwipe();
    }
}











