package com.thesubgraph.network.view.common

data class PaginationState(
    val page: Int = 1,
    val reachedEnd: Boolean = false,
    val pageSize: Int = 100
)
