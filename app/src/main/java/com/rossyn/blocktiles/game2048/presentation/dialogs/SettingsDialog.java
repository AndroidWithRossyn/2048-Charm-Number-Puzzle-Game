package com.rossyn.blocktiles.game2048.presentation.dialogs;

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
import com.rossyn.blocktiles.game2048.databinding.DialogSettingBinding;

import java.util.Objects;

public class SettingsDialog extends Dialog {

    public interface SettingsDialogListener {
        void playSound();
    }

    private DialogSettingBinding binding;
    private final SettingsDialogListener listener;
    private final SharedPref sharedPref;
    private final Animation scaleAnim;

    public SettingsDialog(@NonNull Context context, @NonNull SharedPref sharedPref, @NonNull SettingsDialogListener listener) {
        super(context);
        this.sharedPref = sharedPref;
        this.listener = listener;
        scaleAnim = AnimationUtils.loadAnimation(context, R.anim.scale_animation);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DialogSettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        Objects.requireNonNull(getWindow())
                .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setCancelable(true);


        if (sharedPref.getBoolean(SharedPref.MUTE_MUSIC)) {
            binding.radiogroupMusicSelect.check(binding.musicOn.getId());
            binding.musicOn.setTextColor(Color.rgb(90, 85, 83));
        } else {
            binding.radiogroupMusicSelect.check(binding.musicOff.getId());
            binding.musicOff.setTextColor(Color.rgb(90, 85, 83));
        }

        binding.radiogroupMusicSelect.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == binding.musicOn.getId()) {
                binding.musicOn.setTextColor(Color.rgb(90, 85, 83));
                binding.musicOff.setTextColor(Color.rgb(167, 168, 168));
                sharedPref.setBoolean(SharedPref.MUTE_SOUNDS, false);
            } else {
                binding.musicOff.setTextColor(Color.rgb(90, 85, 83));
                binding.musicOn.setTextColor(Color.rgb(167, 168, 168));
                sharedPref.setBoolean(SharedPref.MUTE_MUSIC, true);
            }
            listener.playSound();
        });

        if (sharedPref.getBoolean(SharedPref.MUTE_SOUNDS)) {
            binding.radiogroupSoundSelect.check(binding.soundOn.getId());
            binding.soundOn.setTextColor(Color.rgb(90, 85, 83));
        } else {
            binding.radiogroupSoundSelect.check(binding.soundOff.getId());
            binding.soundOff.setTextColor(Color.rgb(90, 85, 83));
        }

        binding.radiogroupSoundSelect.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == binding.soundOn.getId()) {
                sharedPref.setBoolean(SharedPref.MUTE_SOUNDS, false);
                binding.soundOn.setTextColor(Color.rgb(90, 85, 83));
                binding.soundOff.setTextColor(Color.rgb(167, 168, 168));
            } else {
                sharedPref.setBoolean(SharedPref.MUTE_SOUNDS, true);
                binding.soundOff.setTextColor(Color.rgb(90, 85, 83));
                binding.soundOn.setTextColor(Color.rgb(167, 168, 168));
            }
            listener.playSound();
        });


        binding.closeButton.startAnimation(scaleAnim);
        binding.closeButton.setOnClickListener(v -> {
            listener.playSound();
           dismiss();
        });
    }

}
