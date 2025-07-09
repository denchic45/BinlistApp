package com.denchic45.binlist.data.api.bin

import com.denchic45.binlist.data.api.bin.model.BinDetailsResponse
import com.denchic45.binlist.data.api.util.safeApiCall
import com.denchic45.binlist.domain.model.NetworkResult
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject

class BinListApi(private val client: HttpClient) {
    private val json = Json
    suspend fun getBinDetails(bin: String): NetworkResult<BinDetailsResponse?> {
        return client.safeApiCall(
            request = { get("https://lookup.binlist.net/$bin") },
            map = {
                json.parseToJsonElement(bodyAsText())
                    .takeIf { element -> element.jsonObject["number"] !is JsonNull }
                    ?.let { json.decodeFromJsonElement(it) }
            })
    }
}