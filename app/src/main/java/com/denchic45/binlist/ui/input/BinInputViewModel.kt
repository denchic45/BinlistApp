package com.denchic45.binlist.ui.input

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.denchic45.binlist.data.api.bin.model.BinDetailsResponse
import com.denchic45.binlist.domain.model.ApiError
import com.denchic45.binlist.domain.model.NoConnection
import com.denchic45.binlist.domain.model.ThrowableError
import com.denchic45.binlist.domain.usecase.FindBinDetailsUseCase
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class BinInputViewModel(
    private val findBinDetailsUseCase: FindBinDetailsUseCase
) : ViewModel() {
    var input by mutableStateOf("220020")
        private set

    var isValid by mutableStateOf(true)
        private set

    private val _binDetailsUiState = MutableStateFlow<BinDetailsUiState>(
        BinDetailsUiState.Success(
            Json.decodeFromString("""
                {
                  "number" : { },
                  "scheme" : "mir",
                  "type" : "debit",
                  "brand" : "Classic",
                  "country" : {
                    "numeric" : "643",
                    "alpha2" : "RU",
                    "name" : "Russian Federation (the)",
                    "emoji" : "🇷🇺",
                    "currency" : "RUB",
                    "latitude" : 60,
                    "longitude" : 100
                  },
                  "bank" : {
                    "name" : "Joint Stock Commercial Bank Moscow Industrial Bank",
                     "url": "www.jyskebank.dk",
                     "phone": "+4589893300",
                     "city": "Hjørring"
                  }
                }
            """.trimIndent())
        )
    )
    val binDetailsUiState = _binDetailsUiState.asStateFlow()

    fun onInputChange(input: String) {
        isValid = input.length >= 6
        this.input = input
    }

    fun onFindClick() {
        viewModelScope.launch {
            _binDetailsUiState.value = BinDetailsUiState.Loading
            findBinDetailsUseCase(input)
                .onSuccess {
                    _binDetailsUiState.value = it?.let { details ->
                        BinDetailsUiState.Success(details)
                    } ?: BinDetailsUiState.NotFound
                }
                .onFailure {
                    _binDetailsUiState.value = when (val failure = it) {
                        NoConnection -> BinDetailsUiState.NoConnection
                        is ApiError -> when (failure.code) {
                            400 -> BinDetailsUiState.InputError
                            429 -> BinDetailsUiState.TooManyRequests
                            in 500..599 -> BinDetailsUiState.ServerError
                            else -> BinDetailsUiState.UnknownError
                        }

                        is ThrowableError -> BinDetailsUiState.UnknownError
                    }
                }
        }
    }
}

sealed interface BinDetailsUiState {
    data object None : BinDetailsUiState
    data object Loading : BinDetailsUiState
    data class Success(val bindDetails: BinDetailsResponse) : BinDetailsUiState
    data object NotFound : BinDetailsUiState
    data object NoConnection : BinDetailsUiState
    data object TooManyRequests : BinDetailsUiState
    data object InputError : BinDetailsUiState
    data object ServerError : BinDetailsUiState
    data object UnknownError : BinDetailsUiState
}