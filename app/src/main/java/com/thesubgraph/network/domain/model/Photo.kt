package com.thesubgraph.network.domain.model

data class Photo(
    val id: String,
    val width: Long,
    val height: Long,
    val color: String,
    val description: String,
    val imageUrls: ImageUrls?
) {
    data class ImageUrls(
        val raw: String,
        val full: String,
        val regular: String,
        val small: String,
        val thumb: String,
    )
}
