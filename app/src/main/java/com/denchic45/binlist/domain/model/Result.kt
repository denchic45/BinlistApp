package com.denchic45.binlist.domain.model

import com.github.michaelbull.result.Result


typealias NetworkResult<T> = Result<T, Failure>

sealed interface Failure
data object NoConnection : Failure
data class ApiError(val code: Int, val body: String) : Failure
data class ThrowableError(val throwable: Throwable) : Failure