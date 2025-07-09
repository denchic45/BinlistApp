package com.denchic45.binlist.domain.usecase

import com.denchic45.binlist.data.repository.BinRepository
import com.denchic45.binlist.domain.model.BinDetailsRequest
import kotlinx.coroutines.flow.Flow

class FindSaveBinDetailsUseCase(private val binRepository: BinRepository) {
    operator fun invoke(): Flow<List<BinDetailsRequest>> {
        return binRepository.findSavedBinDetails()
    }
}