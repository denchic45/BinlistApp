package com.denchic45.binlist.ui.history

import androidx.lifecycle.ViewModel
import com.denchic45.binlist.domain.usecase.FindSaveBinDetailsUseCase


class HistoryViewModel(
    private val findSaveBinDetailsUseCase: FindSaveBinDetailsUseCase
): ViewModel() {
}