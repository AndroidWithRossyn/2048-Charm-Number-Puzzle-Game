package com.rossyn.blocktiles.game2048.presentation.interfaces;

import android.view.SurfaceHolder;

import androidx.annotation.NonNull;

public interface GameListener {
    void surfaceCreated(@NonNull SurfaceHolder holder);

    void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height);

    void surfaceDestroyed(@NonNull SurfaceHolder holder);

    void updateScore(final long score, final long topScore);

    void showGameOver(String score);

    void ShowShufflingMsg();

    void firstScreenTutorial();

    void secondScreenTutorial();

    void thirdScreenTutorial();

    void showAnnouncingMsg(final String msg);

    void playSwipe();
}
