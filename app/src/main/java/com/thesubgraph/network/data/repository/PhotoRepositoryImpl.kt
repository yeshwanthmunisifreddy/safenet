package com.thesubgraph.network.data.repository

import com.thesubgraph.annotations.RepositoryModule
import com.thesubgraph.network.data.remote.ApiService
import com.thesubgraph.network.domain.repository.PhotoRepository
import com.thesubgraph.networking.AccessToken
import com.thesubgraph.networking.SessionState
import com.thesubgraph.networking.data.common.RequestWrapper
import com.thesubgraph.networking.data.common.ValueResult
import com.thesubgraph.network.domain.model.Photo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@RepositoryModule(type = PhotoRepository::class)
class PhotoRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val requestWrapper: RequestWrapper,
    private val sessionState: SessionState,
) : PhotoRepository {
    override fun getPhotos(page: Int, pageSize: Int): Flow<ValueResult<List<Photo>>> {
        return flow {
            sessionState.accessToken = AccessToken("Client-ID", token = "uxQ_VELbYVLsW95L9HsRfHYvScJ2pS0bjRH4FuW-5yo")
            val result = requestWrapper.execute(mapper = { photos ->
                photos.map { it.mapToDomain() } }) {
                apiService.getPhotos(page = page, perSize = pageSize)
            }
            emit(result)
        }
    }
}