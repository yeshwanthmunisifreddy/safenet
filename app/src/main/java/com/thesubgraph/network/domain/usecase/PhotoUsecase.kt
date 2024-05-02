package com.thesubgraph.network.domain.usecase

import com.thesubgraph.annotations.UseModule
import com.thesubgraph.network.domain.repository.PhotoRepository
import com.thesubgraph.networking.data.common.ValueResult
import com.thesubgraph.wallpaper.domain.model.Photo
import kotlinx.coroutines.flow.Flow

@UseModule
class PhotoUseCase(
    private val repository: PhotoRepository,
) {
    fun execute(page: Int, pageSize: Int): Flow<ValueResult<List<Photo>>> {
        return repository.getPhotos(page, pageSize)
    }
}