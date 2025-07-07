package com.denchic45.binlist.data.repository

import com.denchic45.binlist.data.api.bin.BinListApi
import com.denchic45.binlist.data.api.bin.model.BinDetailsResponse
import com.denchic45.binlist.data.database.BinDetailsDao
import com.denchic45.binlist.data.mapper.toResponses
import com.denchic45.binlist.domain.model.NetworkResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BinRepository(
    private val binListApi: BinListApi,
    private val binDetailsDao: BinDetailsDao
) {
    suspend fun findBinDetails(bin: String): NetworkResult<BinDetailsResponse?> {
        return binListApi.getBinDetails(bin)
    }

    fun findSavedBinDetails(): Flow<List<BinDetailsResponse>> {
        return binDetailsDao.getAll().map { it.toResponses() }
    }

    suspend fun removeSavedBinDetails(id: Long) {
        binDetailsDao.deleteById(id)
    }

    suspend fun clearAllSavedBinsDetails() {
        binDetailsDao.deleteAll()
    }
}