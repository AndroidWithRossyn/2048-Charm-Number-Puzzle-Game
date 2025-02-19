package com.rossyn.blocktiles.game2048.presentation.activities;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;

import com.rossyn.blocktiles.game2048.R;
import com.rossyn.blocktiles.game2048.databinding.GameActivityBinding;
import com.rossyn.blocktiles.game2048.presentation.components.GameViewCell;
import com.rossyn.blocktiles.game2048.domain.utils.ThreadMain;
import com.rossyn.blocktiles.game2048.presentation.components.BitmapCreator;
import com.rossyn.blocktiles.game2048.data.prefs.SharedPref;
import com.rossyn.blocktiles.game2048.presentation.dialogs.GameOverDialog;
import com.rossyn.blocktiles.game2048.presentation.dialogs.ScoreboardDialog;
import com.rossyn.blocktiles.game2048.presentation.dialogs.SettingsDialog;
import com.rossyn.blocktiles.game2048.presentation.interfaces.GameListener;

import timber.log.Timber;


public class GameActivity extends BaseActivity implements GameListener {

    private TextView scoreTextView, topScoreTextView;
    private int boardRows, boardCols, boardExponent, gameMode;
    private boolean isTutorialFromMainScreen;

    GameViewCell gameViewCell;

    @Override
    protected void onBackPressedCustom() {
        destroyGameThread();
    }

    private GameActivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boardRows = getIntent().getIntExtra("rows", 4);
        boardCols = getIntent().getIntExtra("cols", 4);
        boardExponent = getIntent().getIntExtra("exponent", 2);
        gameMode = getIntent().getIntExtra("game_mode", 0);
        isTutorialFromMainScreen = getIntent().getBooleanExtra("tutorial", false);
        binding = GameActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, windowInsets) -> {
//            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemGestures());
            v.setPadding(0, 0, 0, 0);
            return windowInsets;
        });

        changeLayout();


        binding.ibHome.setOnClickListener(v -> {
            startActivity(new Intent(GameActivity.this, HomeActivity.class));
            finish();
        });

        gameViewCell = binding.gameViewCell;
        gameViewCell.initSwipeListener(binding.relativeLayout);
        gameViewCell.setOnCustomEventListener(this);

        swipeSoundPool = new SoundPool.Builder().setMaxStreams(5).build();
        swipeSoundId = swipeSoundPool.load(this, R.raw.swipe, 1);

        binding.btnSettings.setOnClickListener(v -> {
            playClick();
            SettingsDialog settingsDialog = new SettingsDialog(GameActivity.this, sharedPref, this::playClick);
            settingsDialog.show();
        });


        binding.btnScoreBoard.startAnimation(scaleAnim);
        binding.btnScoreBoard.setOnClickListener(v -> {
            playClick();
            ScoreboardDialog scoreboardDialog = new ScoreboardDialog(this, this::playClick);
            scoreboardDialog.show();
        });


        binding.ibReset.setOnClickListener(v -> {
            playClick();
            gameViewCell.resetGame();
        });

        binding.ibUndo.setOnClickListener(v -> {
            playClick();
            gameViewCell.undoGame();
        });

    }



    public int getBoardRows() {
        return boardRows;
    }

    public int getBoardCols() {
        return boardCols;
    }

    public int getBoardExponent() {
        return boardExponent;
    }

    public int getGameMode() {
        return gameMode;
    }

    public boolean isTutorial() {
        if (sharedPref.getBoolean(SharedPref.TUTORIAL_PLAYED) || isTutorialFromMainScreen) {
            sharedPref.setBoolean(SharedPref.TUTORIAL_PLAYED, true);
            return true;
        } else {
            return false;
        }
    }


    private void changeLayout() {
        ViewGroup.LayoutParams params = binding.gameLinerLayout.getLayoutParams();
        params.width = Resources.getSystem().getDisplayMetrics().widthPixels;
        params.height = Resources.getSystem().getDisplayMetrics().heightPixels;
        double difference = (double) boardCols / boardRows;

        if (boardRows == 3 && boardCols == 3) {
            params.width = params.height = (int) (params.width * 0.8);
        }
        if (boardRows == 4 && boardCols == 4) {
            params.width = params.height = (int) (params.width * 0.85);
        }
        if (boardRows != boardCols) {
            params.width = params.height = (int) (params.width * 1.1);
            params.width = (int) (params.width * difference);
        }
        binding.gameLinerLayout.setLayoutParams(params);
    }


    private ThreadMain thread;


    public void destroyGameThread() {
        boolean retry = true;
        while (retry) {
            try {
                thread.setRunning(false);
                thread.join();
                retry = false;
                BitmapCreator bitmapCreator = new BitmapCreator(this);
                bitmapCreator.clearBitmapArray();
                finish();
            } catch (InterruptedException e) {
                Timber.e(e);
            }
        }
    }


    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        thread = new ThreadMain(holder, gameViewCell);
        thread.setRunning(true);
        thread.start();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        thread.setSurfaceHolder(holder);
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        destroyGameThread();
    }


    @Override
    public void updateScore(final long score, final long topScore) {
        this.scoreTextView = binding.currentScoreTextview;
        this.topScoreTextView = binding.bestScoreTextview;

        runOnUiThread(() -> {
            if (score == 0) {
                scoreTextView.setText(getString(R.string.start));
            } else {
                scoreTextView.setText(String.valueOf(score));
            }
            topScoreTextView.setText(String.valueOf(topScore));
        });

    }


    @Override
    public void ShowShufflingMsg() {
        runOnUiThread(() -> {
            gameViewCell.dialogOpen = true;
            binding.announcingMsg.setText(getResources().getString(R.string.shuffle));
            binding.announcingMsg.setTextColor(getResources().getColor(R.color.value2));
            binding.announcingMsg.setVisibility(View.VISIBLE);
            binding.backgroundDark.setVisibility(View.VISIBLE);
            new CountDownTimer(1000, 100) {
                @Override
                public void onTick(long millisUntilFinished) {
                }

                @Override
                public void onFinish() {
                    binding.announcingMsg.setVisibility(View.GONE);
                    binding.backgroundDark.setVisibility(View.GONE);
                    gameViewCell.dialogOpen = false;
                }
            }.start();
        });
    }

    @Override
    public void firstScreenTutorial() {
        runOnUiThread(() -> {
            binding.backgroundDark.setVisibility(View.VISIBLE);
            binding.tutorialTextview.setVisibility(View.VISIBLE);
            binding.tutorialTextview.setText(getString(R.string.tutorial_first_line));
        });
    }

    @Override
    public void secondScreenTutorial() {
        runOnUiThread(() -> binding.tutorialTextview.setText(getString(R.string.tutorial_second_line)));
    }

    @Override
    public void thirdScreenTutorial() {
        binding.buttonEndTutorial.startAnimation(scaleAnim);
        runOnUiThread(() -> {
            binding.tutorialTextview.setText(getString(R.string.tutorial_third_line));
            binding.buttonEndTutorial.setVisibility(View.VISIBLE);
            gameViewCell.dialogOpen = true;
            binding.buttonEndTutorial.setOnClickListener(v -> {
                if (isTutorialFromMainScreen) {
                    onBackPressedCustom();
                } else {
                    playClick();
                    binding.buttonEndTutorial.clearAnimation();
                    binding.backgroundDark.setVisibility(View.GONE);
                    binding.tutorialTextview.setVisibility(View.GONE);
                    binding.buttonEndTutorial.setVisibility(View.GONE);
                    gameViewCell.informFinish();
                }
            });


        });
    }

    @Override
    public void showAnnouncingMsg(String msg) {
        runOnUiThread(() -> {
            binding.announcingMsg.setText(msg);
            gameViewCell.dialogOpen = true;
            binding.announcingMsg.setVisibility(View.VISIBLE);
            binding.backgroundDark.setVisibility(View.VISIBLE);
            binding.announcingMsg.setTextSize(60);

            new CountDownTimer(2000, 100) {
                int count = 0;

                @Override
                public void onTick(long millisUntilFinished) {
                    switch (count) {
                        case 0:
                            binding.announcingMsg.setTextColor(getResources().getColor(R.color.value2));
                            count++;
                            break;
                        case 1:
                            binding.announcingMsg.setTextColor(getResources().getColor(R.color.value4));
                            count++;
                            break;
                        case 2:
                            binding.announcingMsg.setTextColor(getResources().getColor(R.color.value8));
                            count++;
                            break;
                        default:
                            binding.announcingMsg.setTextColor(getResources().getColor(R.color.value16));
                            count = 0;
                            break;
                    }
                }

                @Override
                public void onFinish() {
                    binding.announcingMsg.setVisibility(View.GONE);
                    binding.backgroundDark.setVisibility(View.GONE);
                    gameViewCell.dialogOpen = false;
                }
            }.start();
        });
    }

    private SoundPool swipeSoundPool;
    private int swipeSoundId = 0;

    @Override
    public void playSwipe() {
        if (sharedPref.getBoolean(SharedPref.MUTE_SOUNDS)) {
            swipeSoundPool.play(swipeSoundId, 1f, 1f, 0, 0, 1f);
        }
    }

    @Override
    public void showGameOver(String score) {
        runOnUiThread(() -> {
            GameOverDialog gameOverDialog = new GameOverDialog(GameActivity.this, score, () -> {
                playClick();
                gameViewCell.gameOver();
            });
            gameOverDialog.show();
        });
    }
}








