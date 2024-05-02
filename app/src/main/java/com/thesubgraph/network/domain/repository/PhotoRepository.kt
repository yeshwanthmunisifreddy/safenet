package com.thesubgraph.network.domain.repository

import com.thesubgraph.networking.data.common.ValueResult
import com.thesubgraph.wallpaper.domain.model.Photo
import kotlinx.coroutines.flow.Flow

interface PhotoRepository {
    fun getPhotos(page: Int, pageSize: Int): Flow<ValueResult<List<Photo>>>
}