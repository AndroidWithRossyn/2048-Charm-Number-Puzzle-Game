package com.rossyn.blocktiles.game2048.presentation.activities;


import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.rossyn.blocktiles.game2048.R;


public class InfoActivity extends BaseActivity {

    @Override
    protected void onBackPressedCustom() {
        finish();
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_activity);


        Animation scaleAnim = AnimationUtils.loadAnimation(InfoActivity.this, R.anim.scale_animation);
        Button btnClose = findViewById(R.id.btn_close_about);
        btnClose.startAnimation(scaleAnim);


        btnClose.setOnClickListener(v -> {
            finish();
            playClick();
        });

        TextView version = findViewById(R.id.version);
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), PackageManager.GET_ACTIVITIES);
            version.setText("v" + info.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            version.setVisibility(View.GONE);
            throw new RuntimeException(e);
        }


    }
}