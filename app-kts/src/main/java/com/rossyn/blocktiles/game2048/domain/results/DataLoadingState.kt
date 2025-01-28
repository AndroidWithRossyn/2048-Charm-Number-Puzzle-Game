package com.rossyn.blocktiles.game2048.domain.results

sealed class DataLoadingState {
    data object Loading : DataLoadingState()
    data object Success : DataLoadingState()
    data class Error(val message: String) : DataLoadingState()
}