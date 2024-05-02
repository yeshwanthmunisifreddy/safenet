package com.thesubgraph.networking.data.serialization.common

data class ErrorDto(
    val code: String?,
    val status: String?,
    val message: String?,
    val timestamp: String?,
    val errors: Map<String, List<String>>?,
)