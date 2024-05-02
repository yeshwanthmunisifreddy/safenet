package com.thesubgraph.networking.data.serialization.common

interface ApplicationError

data class ValidationError(
    val key: String,
    val messages: List<String>,
) : ApplicationError

enum class NetworkError : ApplicationError {
    ServerNotResponding,
    ServerNotFound,
    RequestTimedOut,
    NoInternet,
}

enum class WebServiceError : ApplicationError {
    Authentication,
    Authorization,
    DefaultsNotLoaded,
    ServerError,
    EncodeFailed,
    DecodeFailed,
    Custom,
    Unknown,
}

data class ErrorModel(
    val type: ApplicationError,
    val message: String,
    val code: String = "",
    val errors: List<ValidationError> = listOf(),
) {
    val fullMessage: String get() {
        return "$message\n${ errors.flatMap { it.messages }.joinToString("\n") }"
    }
}
