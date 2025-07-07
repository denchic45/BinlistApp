package com.denchic45.binlist.data.api.util

import com.denchic45.binlist.domain.model.ApiError
import com.denchic45.binlist.domain.model.NetworkResult
import com.denchic45.binlist.domain.model.NoConnection
import com.denchic45.binlist.domain.model.ThrowableError
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import io.ktor.util.network.UnresolvedAddressException
import java.net.ConnectException
import java.net.UnknownHostException

suspend inline fun <reified T> HttpClient.safeApiCall(
    request: suspend HttpClient.() -> HttpResponse,
): NetworkResult<T> {
    return safeApiCall(request) { body() }
}

suspend inline fun <reified T> HttpClient.safeApiCall(
    request: suspend HttpClient.() -> HttpResponse,
    map: HttpResponse.() -> T
): NetworkResult<T> {
    return try {
        val response = request()
        if (response.status.isSuccess()) {
            Ok(map(response))
        } else {
            Err(ApiError(response.status.value, response.bodyAsText()))
        }
    } catch (t: Throwable) {
        Err(
            when (t) {
                is ConnectException,
                is UnknownHostException,
                is UnresolvedAddressException -> NoConnection
                else -> ThrowableError(t)
            }
        )
    }
}