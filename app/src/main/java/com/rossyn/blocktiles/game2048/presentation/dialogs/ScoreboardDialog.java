package com.rossyn.blocktiles.game2048.presentation.dialogs;

import static com.rossyn.blocktiles.game2048.data.prefs.SharedPref.TOP_SCORE;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;

import com.rossyn.blocktiles.game2048.R;
import com.rossyn.blocktiles.game2048.data.prefs.SharedPref;
import com.rossyn.blocktiles.game2048.databinding.DialogScoreboardBinding;
import com.rossyn.blocktiles.game2048.domain.models.ScoreModel;
import com.rossyn.blocktiles.game2048.presentation.adapter.ScoreAdapter;

import java.util.ArrayList;
import java.util.Collections;

public class ScoreboardDialog extends Dialog {
    private DialogScoreboardBinding binding;
    private final SharedPref sharedPref;
    private final ScoreboardDialogListener listener;

    private final Animation rightInAnim;
    private final Animation leftInAnim;
    private final Animation rightOutAnim;
    private final Animation leftOutAnim;
    private final Animation scaleAnim;

    private ScoreAdapter scoreAdapterClassic;
    private ScoreAdapter scoreAdapterBlocks;
    private ScoreAdapter scoreAdapterShuffle;

    private final int[] index = {0};


    public interface ScoreboardDialogListener {
        void playSound();
    }

    public ScoreboardDialog(@NonNull Context context, @NonNull ScoreboardDialogListener listener) {
        super(context);
        this.sharedPref = new SharedPref(context);
        this.listener = listener;
        rightInAnim = AnimationUtils.loadAnimation(context, R.anim.slide_in_right_side);
        leftInAnim = AnimationUtils.loadAnimation(context, R.anim.slide_in_left_side);
        rightOutAnim = AnimationUtils.loadAnimation(context, R.anim.slide_out_right_side);
        leftOutAnim = AnimationUtils.loadAnimation(context, R.anim.slide_out_left_side);
        scaleAnim = AnimationUtils.loadAnimation(context, R.anim.scale_animation);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DialogScoreboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        int width = (int) (getContext().getResources().getDisplayMetrics().widthPixels * 0.90);
        int height = (int) (getContext().getResources().getDisplayMetrics().heightPixels * 0.90);
        if (getWindow() != null) {
            getWindow().setLayout(width, height);
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }


        ArrayList<ScoreModel> classicScoreModels = createClassicArrayList("0");
        ArrayList<ScoreModel> blocksScoreModels = createClassicArrayList("1");
        ArrayList<ScoreModel> shuffleScoreModels = createClassicArrayList("2");


        scoreAdapterClassic = new ScoreAdapter(classicScoreModels);
        scoreAdapterBlocks = new ScoreAdapter(blocksScoreModels);
        scoreAdapterShuffle = new ScoreAdapter(shuffleScoreModels);


        binding.listviewScoreBoard.setAdapter(scoreAdapterClassic);
        binding.textviewModeType.setText(getContext().getString(R.string.mode_classic));


        binding.rightBtn.startAnimation(scaleAnim);
        binding.rightBtn.setOnClickListener(v -> {
            if (index[0] == 2) {
                index[0] = 0;
            } else {
                index[0]++;
            }
            leftOutAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    switch (index[0]) {
                        case 0:
                            playClick();
                            binding.listviewScoreBoard.setAdapter(scoreAdapterClassic);
                            binding.textviewModeType.setText(getContext().getString(R.string.mode_classic));
                            break;
                        case 1:
                            playClick();
                            binding.listviewScoreBoard.setAdapter(scoreAdapterBlocks);
                            binding.textviewModeType.setText(getContext().getString(R.string.mode_blocks));
                            break;
                        case 2:
                            playClick();
                            binding.listviewScoreBoard.setAdapter(scoreAdapterShuffle);
                            binding.textviewModeType.setText(getContext().getString(R.string.mode_shuffle));
                            break;
                    }
                    binding.listviewScoreBoard.startAnimation(rightInAnim);
                }
            });
            binding.listviewScoreBoard.startAnimation(leftOutAnim);
        });


        binding.leftBtn.startAnimation(scaleAnim);
        binding.leftBtn.setOnClickListener(v -> {
            if (index[0] == 0) {
                index[0] = 2;
            } else {
                index[0]--;
            }
            rightOutAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    switch (index[0]) {
                        case 0:
                            playClick();
                            binding.listviewScoreBoard.setAdapter(scoreAdapterClassic);
                            binding.textviewModeType.setText(getContext().getString(R.string.mode_classic));
                            break;
                        case 1:
                            playClick();
                            binding.listviewScoreBoard.setAdapter(scoreAdapterBlocks);
                            binding.textviewModeType.setText(getContext().getString(R.string.mode_blocks));
                            break;
                        case 2:
                            playClick();
                            binding.listviewScoreBoard.setAdapter(scoreAdapterShuffle);
                            binding.textviewModeType.setText(getContext().getString(R.string.mode_shuffle));
                            break;
                    }
                    binding.listviewScoreBoard.startAnimation(leftInAnim);
                }
            });
            binding.listviewScoreBoard.startAnimation(rightOutAnim);
        });


        binding.closeButton.setAnimation(scaleAnim);
        binding.closeButton.setOnClickListener(v -> {
            playClick();
            dismiss();
        });
    }


    private void playClick() {
        listener.playSound();
    }


    /**
     * Creates an ArrayList of ScoreModel objects for the classic game mode.
     *
     * @param gameMode The game mode identifier to differentiate between different game modes.
     * @return An ArrayList of ScoreModel objects sorted by score in descending order.
     */
    public ArrayList<ScoreModel> createClassicArrayList(String gameMode) {

        ArrayList<ScoreModel> scoreModels = new ArrayList<>();
        // Add ScoreModel objects for different game board sizes
        scoreModels.add(new ScoreModel("6x5", sharedPref.getLong(TOP_SCORE + gameMode + 6 + 5), R.drawable.icon_trophy_empty));
        scoreModels.add(new ScoreModel("5x4", sharedPref.getLong(TOP_SCORE + gameMode + 5 + 4), R.drawable.icon_trophy_empty));
        scoreModels.add(new ScoreModel("5x3", sharedPref.getLong(TOP_SCORE + gameMode + 5 + 3), R.drawable.icon_trophy_empty));
        scoreModels.add(new ScoreModel("4x3", sharedPref.getLong(TOP_SCORE + gameMode + 4 + 3), R.drawable.icon_trophy_empty));
        scoreModels.add(new ScoreModel("8x8", sharedPref.getLong(TOP_SCORE + gameMode + 8 + 8), R.drawable.icon_trophy_empty));
        scoreModels.add(new ScoreModel("6x6", sharedPref.getLong(TOP_SCORE + gameMode + 6 + 6), R.drawable.icon_trophy_empty));
        scoreModels.add(new ScoreModel("5x5", sharedPref.getLong(TOP_SCORE + gameMode + 5 + 5), R.drawable.icon_trophy_empty));
        scoreModels.add(new ScoreModel("4x4", sharedPref.getLong(TOP_SCORE + gameMode + 4 + 4), R.drawable.icon_trophy_empty));
        scoreModels.add(new ScoreModel("3x3", sharedPref.getLong(TOP_SCORE + gameMode + 3 + 3), R.drawable.icon_trophy_empty));

        // Sort the score models by score in ascending order
        scoreModels.sort((p1, p2) -> (int) (p1.getScore() - p2.getScore()));
        // Reverse the order to get descending order
        Collections.reverse(scoreModels);

        // Set icons for the top three scores if they are not zero
        if (scoreModels.get(0).getScore() != 0) {
            scoreModels.get(0).setIcon(R.drawable.icon_trophy_gold);
        }
        if (scoreModels.get(1).getScore() != 0) {
            scoreModels.get(1).setIcon(R.drawable.icon_trophy_silver);
        }
        if (scoreModels.get(2).getScore() != 0) {
            scoreModels.get(2).setIcon(R.drawable.icon_trophy_bronze);
        }

        return scoreModels;
    }
}
