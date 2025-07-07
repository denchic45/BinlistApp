package com.denchic45.binlist.domain.usecase

import com.denchic45.binlist.data.api.bin.model.BinDetailsResponse
import com.denchic45.binlist.data.repository.BinRepository
import com.denchic45.binlist.domain.model.NetworkResult

class FindBinDetailsUseCase(private val repository: BinRepository) {
    suspend operator fun invoke(num: String): NetworkResult<BinDetailsResponse?> {
        return repository.findBinDetails(num)
    }
}