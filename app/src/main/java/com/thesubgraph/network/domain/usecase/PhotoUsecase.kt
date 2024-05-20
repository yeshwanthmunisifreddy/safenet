package com.thesubgraph.network.domain.usecase

import com.thesubgraph.annotations.UseCaseModule
import com.thesubgraph.network.domain.repository.PhotoRepository
import com.thesubgraph.safenet.data.common.ValueResult
import com.thesubgraph.network.domain.model.Photo
import kotlinx.coroutines.flow.Flow

@UseCaseModule
class PhotoUseCase(
    private val repository: PhotoRepository,
) {
    fun execute(page: Int, pageSize: Int): Flow<ValueResult<List<Photo>>> {
        return repository.getPhotos(page, pageSize)
    }
}