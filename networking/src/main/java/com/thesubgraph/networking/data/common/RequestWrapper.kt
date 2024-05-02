package com.thesubgraph.networking.data.common

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.thesubgraph.networking.data.serialization.common.ErrorMapper
import com.thesubgraph.networking.data.serialization.common.WebServiceError
import com.thesubgraph.networking.data.serialization.common.toDomain
import retrofit2.Response
import javax.inject.Inject

class RequestWrapper @Inject constructor(
    private val gson: Gson,
    private val context: Context,
) {
    suspend fun <T, U> execute(
        mapper: (T) -> U?,
        apiCall: suspend () -> Response<T>,
    ): ValueResult<U> {
        val result = execute(apiCall)
        val data = result.value?.let { mapper(it) }

        return data?.let { ValueResult.Success(it) }
            ?: (result as? ValueResult.Failure)?.let { ValueResult.Failure(result.error) }
            ?: ValueResult.Failure(WebServiceError.DecodeFailed.toDomain(context))
    }

    private suspend fun <T> execute(
        apiCall: suspend () -> Response<T>,
    ): ValueResult<T> {
        try {
            val response = apiCall()
            if (response.isSuccessful) {
                val body = response.body()
                body?.let {
                    return ValueResult.Success(body)
                }
            } else {
                val error = ErrorMapper(response, gson).mapToDomain(context)
                return ValueResult.Failure(error)
            }
        } catch (e: Exception) {
            Log.e("ApiResponseException", "$e")
        }

        return ValueResult.Failure(WebServiceError.Unknown.toDomain(context))
    }

}
