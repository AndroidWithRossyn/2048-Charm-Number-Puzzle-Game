package com.rossyn.blocktiles.game2048.presentation.activities

import android.content.Intent
import android.os.Bundle
import androidx.core.view.ViewCompat
import androidx.lifecycle.lifecycleScope
import com.rossyn.blocktiles.game2048.databinding.EntryActivityBinding
import com.rossyn.blocktiles.game2048.domain.results.DataLoadingState
import com.rossyn.blocktiles.game2048.presentation.utils.enableEdgeToEdgeAppTrans
import com.rossyn.blocktiles.game2048.utils.JUST_A_MOMENT
import com.rossyn.blocktiles.game2048.utils.WAIT_WHAT
import com.rossyn.blocktiles.game2048.utils.letMeThink
import com.rossyn.blocktiles.game2048.utils.takeABreath
import com.rossyn.easytoast.kts.ToastUtils.showToast
import dagger.hilt.android.AndroidEntryPoint
import dev.funkymuse.animations.attentionFlash
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import timber.log.Timber

@AndroidEntryPoint
class EntryActivity : BaseActivity() {

    override fun onBackPressedCustom() {
        finish()
    }

    private val loadingState = MutableStateFlow<DataLoadingState>(DataLoadingState.Loading)


    private lateinit var binding: EntryActivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdgeAppTrans()
        binding = EntryActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            v.setPadding(0, 0, 0, 0)
            insets
        }

        lifecycleScope.launch {
            loadingState.collect { state ->
                when (state) {
                    is DataLoadingState.Loading -> Timber.d("Loading data...")
                    is DataLoadingState.Success -> moveToNextActivity()
                    is DataLoadingState.Error -> showToast(state.message)
                }
            }
        }

        loadData()
    }

    private fun loadData() {
        loadingState.value = DataLoadingState.Loading

        lifecycleScope.launch {
            try {
                withTimeout(JUST_A_MOMENT + WAIT_WHAT) {
                    letMeThink()
                    loadingState.value = DataLoadingState.Success
                }
            } catch (e: TimeoutCancellationException) {
                loadingState.value = DataLoadingState.Error("Loading timeout: ${e.message}")
            } catch (e: Exception) {
                loadingState.value = DataLoadingState.Error("Error loading data: ${e.message}")
            }
        }
    }

    private fun moveToNextActivity() {
        lifecycleScope.launch {
            binding.eaTvMain.attentionFlash().start()
            binding.eaTvCopyright.attentionFlash().start()
            takeABreath()
            val intent = Intent(this@EntryActivity, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

}