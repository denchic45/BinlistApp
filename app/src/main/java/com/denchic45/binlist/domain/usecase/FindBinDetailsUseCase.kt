package com.denchic45.binlist.domain.usecase

import com.denchic45.binlist.data.repository.BinRepository
import com.denchic45.binlist.domain.model.BinDetailsRequest
import com.denchic45.binlist.domain.model.NetworkResult

class FindBinDetailsUseCase(private val repository: BinRepository) {
    suspend operator fun invoke(num: String): NetworkResult<BinDetailsRequest?> {
        return repository.findBinDetails(num)
    }
}