package com.denchic45.binlist.data.repository

import com.denchic45.binlist.data.api.bin.BinListApi
import com.denchic45.binlist.data.api.bin.model.BinDetailsResponse
import com.denchic45.binlist.domain.model.NetworkResult

class BinRepository(private val binListApi: BinListApi) {
    suspend fun findBinDetails(bin: String): NetworkResult<BinDetailsResponse?> {
       return binListApi.getBinDetails(bin)
    }
}