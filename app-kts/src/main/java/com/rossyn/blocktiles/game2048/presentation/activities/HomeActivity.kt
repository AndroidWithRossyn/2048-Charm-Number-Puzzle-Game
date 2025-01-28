package com.rossyn.blocktiles.game2048.presentation.activities

import android.os.Bundle
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.rossyn.blocktiles.game2048.databinding.HomeActivityBinding
import com.rossyn.blocktiles.game2048.presentation.utils.enableEdgeToEdgeAppTrans
import com.rossyn.blocktiles.game2048.presentation.utils.scaleAnim
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : BaseActivity(), View.OnClickListener {

    override fun onBackPressedCustom() {
        finish()
    }

    private lateinit var binding: HomeActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdgeAppTrans()
        binding = HomeActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            v.setPadding(0, 0, 0, 0)
            insets
        }
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())


        listOf(
            binding.btnNewGame,
            binding.btnLoadGame,
            binding.btnSettings,
            binding.btnScoreBoard,
            binding.btnAbout,
            binding.btnRate,
            binding.btnMoreGames
        ).forEach {
            it.scaleAnim()
            it.setOnClickListener(this@HomeActivity)
        }


    }

    override fun onClick(v: View?) {
        when (v) {
            binding.btnNewGame -> {

            }
        }
    }

}