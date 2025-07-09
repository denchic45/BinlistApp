package com.denchic45.binlist.data.repository

import com.denchic45.binlist.data.api.bin.BinListApi
import com.denchic45.binlist.data.database.BinDetailsDao
import com.denchic45.binlist.data.mapper.toBinDetails
import com.denchic45.binlist.data.mapper.toBinDetailsEntity
import com.denchic45.binlist.data.mapper.toBinsDetails
import com.denchic45.binlist.domain.model.BinDetailsRequest
import com.denchic45.binlist.domain.model.NetworkResult
import com.github.michaelbull.result.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BinRepository(
    private val binListApi: BinListApi,
    private val binDetailsDao: BinDetailsDao
) {
    suspend fun findBinDetails(bin: String): NetworkResult<BinDetailsRequest?> {
        return binListApi.getBinDetails(bin)
            .map { response ->
                response?.let {
                    val id = binDetailsDao.insert(response.toBinDetailsEntity(bin))
                    binDetailsDao.getById(id).toBinDetails()
                }
            }
    }

    fun findSavedBinDetails(): Flow<List<BinDetailsRequest>> {
        return binDetailsDao.getAll().map { it.toBinsDetails() }
    }
}