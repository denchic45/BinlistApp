package com.denchic45.binlist.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.denchic45.binlist.domain.model.BinDetailsRequest
import com.denchic45.binlist.domain.usecase.FindSaveBinDetailsUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn


class HistoryViewModel(
    findSaveBinDetailsUseCase: FindSaveBinDetailsUseCase
) : ViewModel() {
    val uiState = findSaveBinDetailsUseCase().map {
        if (it.isNotEmpty()) HistoryUiState.Success(it)
        else HistoryUiState.Empty
    }.stateIn(viewModelScope, SharingStarted.Lazily, HistoryUiState.Loading)
}

sealed interface HistoryUiState {
    data object Loading : HistoryUiState
    data class Success(val requests: List<BinDetailsRequest>) : HistoryUiState
    data object Empty : HistoryUiState
}