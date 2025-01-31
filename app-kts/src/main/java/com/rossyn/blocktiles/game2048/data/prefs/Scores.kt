package com.rossyn.blocktiles.game2048.data.prefs

import android.content.SharedPreferences

class Scores(
    var score: Long,
    private var prefs: SharedPreferences,
    gameMode: Int,
    rows: Int,
    cols: Int
) {

    var prevScore: Long = 0
    var TopScore: Long = 0
    var prevTopScore: Long = 0
    var rowsString: String = ""
    var colsString: String = ""
    var gameModeString: String = ""
    var newHighScore = false


    init {
        this.gameModeString = gameMode.toString()
        this.rowsString = rows.toString()
        this.colsString = cols.toString()
        this.TopScore =
            prefs.getLong(SharedPref.TOP_SCORE + gameModeString + rowsString + colsString, 0)
        this.prevTopScore = TopScore
    }


    fun checkTopScore() {
        if (!newHighScore) {
            TopScore =
                prefs.getLong(SharedPref.TOP_SCORE + gameModeString + rowsString + colsString, 0)
            if (score > TopScore) {
                newHighScore = true
                prevTopScore = TopScore
                TopScore = score
            }
        }
    }

    fun updateScoreBoard() {
        prefs.edit().putLong(SharedPref.TOP_SCORE + gameModeString + rowsString + colsString, TopScore).apply()
    }

    fun updateScore(value: Long) {
        prevScore = score
        score = value
        if (!newHighScore) checkTopScore()
        else {
            prevTopScore = TopScore
            TopScore = score
        }
    }

    fun refreshScoreBoard() {
        TopScore = prefs.getLong(SharedPref.TOP_SCORE + gameModeString + rowsString + colsString, 0)
    }

    fun undoScore() {
        score = prevScore
        TopScore = prevTopScore
        if (score < TopScore) {
            newHighScore = false
            updateScoreBoard()
        }
    }


    fun isNewHighScore(): Boolean {
        return newHighScore
    }

    fun resetGame() {
        score = 0
        refreshScoreBoard()
        newHighScore = false
    }

}