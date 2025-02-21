

package com.rossyn.blocktiles.game2048.presentation.activities;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;

import com.rossyn.blocktiles.game2048.R;
import com.rossyn.blocktiles.game2048.databinding.HomeActivityBinding;
import com.rossyn.blocktiles.game2048.presentation.dialogs.ScoreboardDialog;
import com.rossyn.blocktiles.game2048.presentation.dialogs.SettingsDialog;

public class HomeActivity extends BaseActivity {

    @Override
    protected void onBackPressedCustom() {
        finish();
    }

    private HomeActivityBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = HomeActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, windowInsets) -> {
//            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemGestures());
            v.setPadding(0, 0, 0, 0);
            return windowInsets;
        });


        binding.btnNewGame.startAnimation(scaleAnim);
        binding.btnNewGame.setOnClickListener(v -> {
            playClick();
            Intent intent = new Intent(HomeActivity.this, BoardOptionsActivity.class);
            startActivity(intent);
        });


        binding.btnLoadGame.startAnimation(scaleAnim);
        binding.btnLoadGame.setOnClickListener(v -> {
            playClick();
            Intent intent = new Intent(HomeActivity.this, GameActivity.class);
            intent.putExtra("tutorial", true);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right_side, R.anim.slide_out_left_side);
        });

        binding.btnSettings.startAnimation(scaleAnim);
        binding.btnSettings.setOnClickListener(v -> {
            playClick();
            SettingsDialog settingsDialog = new SettingsDialog(this, this::playClick);
            settingsDialog.show();
        });


        binding.btnScoreBoard.startAnimation(scaleAnim);
        binding.btnScoreBoard.setOnClickListener(v -> {
            playClick();
            ScoreboardDialog scoreboardDialog = new ScoreboardDialog(this, this::playClick);
            scoreboardDialog.show();
        });


        binding.btnAbout.startAnimation(scaleAnim);
        binding.btnAbout.setOnClickListener(v -> {
            playClick();
            Intent intent = new Intent(HomeActivity.this, InfoActivity.class);
            startActivity(intent);
        });


        binding.btnRate.startAnimation(scaleAnim);
        binding.btnRate.setOnClickListener(v -> {
            playClick();
            Toast.makeText(this, "PlayStore URL", Toast.LENGTH_SHORT).show();
        });


        binding.btnMoreGames.startAnimation(scaleAnim);
        binding.btnMoreGames.setOnClickListener(v -> {
            playClick();
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.moregamesurl))));
        });

    }


}
