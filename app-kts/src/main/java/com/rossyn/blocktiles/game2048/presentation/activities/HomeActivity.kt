package com.rossyn.blocktiles.game2048.presentation.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.ViewCompat
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
        playClick()
        when (v) {
            binding.btnNewGame -> {
                val intent = Intent(this, BoardOptionsActivity::class.java)
                startActivity(intent)
            }

            binding.btnLoadGame -> {
                val intent = Intent(this, GameActivity::class.java)
                startActivity(intent)
            }

            binding.btnSettings -> {

            }

            binding.btnScoreBoard -> {

            }

            binding.btnAbout -> {
                val intent = Intent(this, InfoActivity::class.java)
                startActivity(intent)
            }

            binding.btnRate -> {

            }

            binding.btnMoreGames -> {

            }
        }
    }

}