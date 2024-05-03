package com.thesubgraph.networking.data.serialization.common

import android.content.Context
import com.google.gson.Gson
import com.thesubgraph.networking.data.common.ContextResponseDomainMapper
import retrofit2.Response
import java.net.UnknownHostException

fun ApplicationError.getMessage(): String {
    return when (this) {
        NetworkError.ServerNotResponding -> "Unable to reach server. Please try again later."
        NetworkError.ServerNotFound -> {
            "Unable to find server. Please check your connection or try again later."
        }
        NetworkError.RequestTimedOut -> "Request timed out. Please try again."
        NetworkError.NoInternet -> "Please check your internet connection and try again."
        WebServiceError.Authorization -> "You are not authorized to access this resource."
        WebServiceError.ServerError -> {
            "Server encountered an error. Please try again or contact us to raise an issue."
        }
        WebServiceError.EncodeFailed -> {
            "Request could not be created. Please try again or contact us to raise an issue."
        }
        WebServiceError.DecodeFailed -> {
            "Response could not be read. Please try again or contact us to raise an issue."
        }
        WebServiceError.Unknown -> "Something went wrong."
        else -> "Something went wrong."
    }
}

fun ApplicationError.toDomain(context: Context): ErrorModel {
    return ErrorModel(this, this.getMessage())
}

fun String.toErrorDomain(): ErrorModel {
    return ErrorModel(WebServiceError.Custom, this)
}

fun getError(status: Int): ApplicationError {
    return when (status) {
        400 -> WebServiceError.EncodeFailed
        401 -> WebServiceError.Authentication
        403 -> WebServiceError.Authorization
        404 -> NetworkError.ServerNotFound
        503, 429 -> NetworkError.ServerNotResponding
        500 -> WebServiceError.ServerError
        408 -> NetworkError.RequestTimedOut
        422 -> WebServiceError.Custom
        else -> WebServiceError.Unknown
    }
}

data class ExceptionMapper(
    val exception: Exception
) : ContextResponseDomainMapper<ErrorModel> {
    override fun mapToDomain(context: Context): ErrorModel {
        return when (exception) {
            is UnknownHostException -> NetworkError.ServerNotFound.toDomain(context)
            else -> WebServiceError.Unknown.toDomain(context)
        }
    }
}

data class ErrorMapper(
    val response: Response<*>,
    private val gson: Gson,
) : ContextResponseDomainMapper<ErrorModel> {

    override fun mapToDomain(context: Context): ErrorModel {
        // TODO: Handle non-http exceptions
        val code = response.code()
        val errorBody = response.errorBody()
        val errorBodyString = response.errorBody()?.string()
        val errorDto = errorBody?.let {
            try {
                val error: ErrorDto = gson.fromJson(errorBodyString, ErrorDto::class.java)
                error
            } catch (e: Exception) {
                val error = WebServiceError.Unknown
                errorBody.close()

                return ErrorModel(type = error, message = error.getMessage())
            }
        }
        val error = getError(code)
        errorBody?.close()

        return ErrorModel(
            type = error,
            code = code,
            message = errorDto?.message ?: error.getMessage(),
            errors = errorDto?.errors?.map {
                return@map ValidationError(it.key, it.value)
            } ?: listOf()
        )
    }

}
