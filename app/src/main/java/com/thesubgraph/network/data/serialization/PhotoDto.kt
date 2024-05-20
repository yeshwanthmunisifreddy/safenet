package com.thesubgraph.network.data.serialization

import com.google.gson.annotations.SerializedName
import com.thesubgraph.safenet.data.common.ResponseDomainMapper
import com.thesubgraph.network.domain.model.Photo

data class PhotoDto(
    @SerializedName("id") val id: String?,
    @SerializedName("width") val width: Long?,
    @SerializedName("height") val height: Long?,
    @SerializedName("color") val color: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("urls") val urls: UrlsDto
) : ResponseDomainMapper<Photo> {
    override fun mapToDomain(): Photo {
        return Photo(
            id = id ?: "",
            width = width ?: 0,
            height = height ?: 0,
            color = color ?: "#ffffff",
            description = description ?: "",
            imageUrls = urls.mapToDomain()
        )
    }

    data class UrlsDto(
        @SerializedName("raw") val raw: String?,
        @SerializedName("full") val full: String?,
        @SerializedName("regular") val regular: String?,
        @SerializedName("small") val small: String?,
        @SerializedName("thumb") val thumb: String?,
    ) : ResponseDomainMapper<Photo.ImageUrls> {
        override fun mapToDomain(): Photo.ImageUrls {
            return Photo.ImageUrls(
                raw = raw ?: "",
                full = full ?: "",
                regular = regular ?: "",
                small = small ?: "",
                thumb = thumb ?: "",
            )
        }
    }
}
