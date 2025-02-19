package com.rossyn.blocktiles.game2048.presentation.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.rossyn.blocktiles.game2048.R;
import com.rossyn.blocktiles.game2048.databinding.SplashActivityBinding;


@SuppressLint("CustomSplashScreen")
public class SplashActivity extends BaseActivity {

    @Override
    protected void onBackPressedCustom() {
        finish();
    }

    private SplashActivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SplashActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemGestures());
            v.setPadding(insets.left, insets.top, insets.right, insets.bottom);
            return windowInsets;
        });


        Animation scaleAnim = AnimationUtils.loadAnimation(this, R.anim.scale_anim_logoimg);
        binding.eaTvMain.startAnimation(scaleAnim);


        handler.postDelayed(splashRunnable, 1500);

    }

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable splashRunnable = () -> {
        navigateTo(HomeActivity.class);
        finish();
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(splashRunnable);
        binding = null;
    }
}

