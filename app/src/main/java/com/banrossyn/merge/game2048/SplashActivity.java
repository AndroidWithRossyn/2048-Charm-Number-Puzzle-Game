package com.banrossyn.merge.game2048;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;


@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
        hideSystemUI();
        Animation scaleAnim = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.scale_anim_logoimg);
        TextView splaceImg= findViewById(R.id.splaceImg);
        splaceImg.startAnimation(scaleAnim);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        }, 1500);

    }
    private void hideSystemUI() {

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        View mContentView = getWindow().getDecorView();

        if (Build.VERSION.SDK_INT >= 30) {
            Objects.requireNonNull(mContentView.getWindowInsetsController()).hide(
                    android.view.WindowInsets.Type.statusBars() | android.view.WindowInsets.Type.navigationBars());
        }
        else {
            mContentView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            );
        }

    }
}

