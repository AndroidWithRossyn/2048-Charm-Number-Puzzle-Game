package com.rossyn.blocktiles.game2048.domain.utils;

import static com.rossyn.blocktiles.game2048.data.prefs.SharedPref.TOP_SCORE;

import android.content.SharedPreferences;


public final class Scores {

    private long score, prevScore;
    private Long TopScore, prevTopScore;
    private final SharedPreferences prefs;
    private final String rowsString, colsString, gameModeString;
    private boolean newHighScore;

    public Scores(Long score, SharedPreferences prefs, int gameMode, int rows, int cols) {

        this.score = score;
        this.prefs = prefs;
        this.gameModeString = Integer.toString(gameMode);
        this.rowsString = Integer.toString(rows);
        this.colsString = Integer.toString(cols);
        this.TopScore = prefs.getLong(TOP_SCORE + gameModeString + rowsString + colsString, 0);
        this.prevTopScore = TopScore;
    }


    public void checkTopScore() {
        if (!newHighScore) {
            TopScore = prefs.getLong(TOP_SCORE + gameModeString + rowsString + colsString, 0);
            if (score > TopScore) {
                newHighScore = true;
                prevTopScore = TopScore;
                TopScore = score;
            }
        }
    }

    public void updateScoreBoard() {

        prefs.edit().putLong(TOP_SCORE + gameModeString + rowsString + colsString, TopScore).apply();
    }

    public void updateScore(Long value) {
        prevScore = score;
        score = value;
        if (!newHighScore)
            checkTopScore();
        else {
            prevTopScore = TopScore;
            TopScore = score;
        }

    }

    public void refreshScoreBoard() {
        TopScore = prefs.getLong(TOP_SCORE + gameModeString + rowsString + colsString, 0);
    }

    public void undoScore() {
        score = prevScore;
        TopScore = prevTopScore;
        if (score < TopScore) {
            newHighScore = false;
            updateScoreBoard();
        }
    }

    public Long getTopScore() {
        return TopScore;
    }

    public Long getScore() {
        return score;
    }

    public boolean isNewHighScore() {
        return newHighScore;
    }

    public void resetGame() {
        score = 0;
        refreshScoreBoard();
        newHighScore = false;
    }


}

