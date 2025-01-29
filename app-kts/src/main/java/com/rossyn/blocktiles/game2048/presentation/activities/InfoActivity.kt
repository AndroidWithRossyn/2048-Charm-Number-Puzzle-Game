package com.rossyn.blocktiles.game2048.presentation.activities

import android.os.Bundle
import androidx.core.view.ViewCompat
import com.rossyn.blocktiles.game2048.databinding.InfoActivityBinding
import com.rossyn.blocktiles.game2048.presentation.utils.enableEdgeToEdgeAppTrans
import com.rossyn.blocktiles.game2048.presentation.utils.scaleAnim
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InfoActivity : BaseActivity() {


    override fun onBackPressedCustom() {
        finish()
    }

    private lateinit var binding: InfoActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdgeAppTrans()
        binding = InfoActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            v.setPadding(0, 0, 0, 0)
            insets
        }

        binding.btnCloseAbout.scaleAnim()
        binding.btnCloseAbout.setOnClickListener {
            onBackPressedCustom()
        }

    }
}