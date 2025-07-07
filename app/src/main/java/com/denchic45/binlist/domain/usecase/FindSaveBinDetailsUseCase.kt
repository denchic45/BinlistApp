package com.denchic45.binlist.domain.usecase

import com.denchic45.binlist.data.api.bin.model.BinDetailsResponse
import com.denchic45.binlist.data.repository.BinRepository
import kotlinx.coroutines.flow.Flow

class FindSaveBinDetailsUseCase(private val binRepository: BinRepository) {
    operator fun invoke(): Flow<List<BinDetailsResponse>> {
        return binRepository.findSavedBinDetails()
    }
}