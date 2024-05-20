package com.thesubgraph.safenet.data.common

import com.thesubgraph.safenet.data.serialization.common.ErrorModel

public interface NetworkResult

sealed class CompletionResult : NetworkResult {
    data object Success : CompletionResult()
    data class Failure(val error: ErrorModel) : CompletionResult()
}

sealed class ValueResult<out T> : NetworkResult { // : ValueResult() {
    data class Success<T>(val data: T) : ValueResult<T>()
    data class Failure(val error: ErrorModel) : ValueResult<Nothing>()

    val value: T? get() = (this as? Success)?.data

    fun toCompletionResult(): CompletionResult = when (this) {
        is Success -> CompletionResult.Success
        is Failure -> CompletionResult.Failure(this.error)
    }

    fun <U> map(mapper: (T) -> U): ValueResult<U> {
        return when (this) {
            is Success -> Success(mapper(data))
            is Failure -> Failure(error)
        }
    }
}

