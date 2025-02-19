package com.rossyn.blocktiles.game2048.presentation.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;

import com.rossyn.blocktiles.game2048.R;
import com.rossyn.blocktiles.game2048.databinding.DialogGameoverBinding;

import java.util.Objects;

public class GameOverDialog extends Dialog {

    public interface DialogResultCallback {
        void onPlayAgain();
    }

    private final DialogResultCallback dialogResultCallback;

    private String score = "0";

    public GameOverDialog(@NonNull Context context, String score, @NonNull DialogResultCallback callback) {
        super(context);
        this.dialogResultCallback = callback;
        this.score = score;
        scaleAnim = AnimationUtils.loadAnimation(context, R.anim.scale_animation);
    }

    private DialogGameoverBinding binding;
    private final Animation scaleAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DialogGameoverBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Objects.requireNonNull(getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        setCancelable(false);
        setCanceledOnTouchOutside(false);

        binding.gameOverScoreNum.setText(score);
        binding.btnPlayAgain.startAnimation(scaleAnim);
        binding.btnPlayAgain.setOnClickListener(v -> {
            dialogResultCallback.onPlayAgain();
            dismiss();
        });

    }
}
