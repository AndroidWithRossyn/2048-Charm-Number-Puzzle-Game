package com.rossyn.blocktiles.game2048.presentation.activities;


import android.os.Bundle;

import androidx.core.view.ViewCompat;

import com.rossyn.blocktiles.game2048.BuildConfig;
import com.rossyn.blocktiles.game2048.databinding.InfoActivityBinding;


public class InfoActivity extends BaseActivity {

    @Override
    protected void onBackPressedCustom() {
        finish();
    }


    private InfoActivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = InfoActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, windowInsets) -> {
//            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemGestures());
            v.setPadding(0, 0, 0, 0);
            return windowInsets;
        });

        binding.btnCloseAbout.startAnimation(scaleAnim);
        binding.btnCloseAbout.setOnClickListener(v -> {
            finish();
            playClick();
        });

        binding.version.setText(String.format("v %s", BuildConfig.VERSION_NAME));


    }
}