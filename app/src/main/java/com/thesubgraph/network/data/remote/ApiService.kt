package com.thesubgraph.network.data.remote

import com.thesubgraph.annotations.Authenticated
import com.thesubgraph.annotations.BaseUrl
import com.thesubgraph.annotations.ServiceModule
import com.thesubgraph.network.data.serialization.PhotoDto
import com.thesubgraph.networking.data.common.WebServiceInterface
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

@ServiceModule
@BaseUrl("https://api.unsplash.com/")
interface ApiService : WebServiceInterface {
    @GET("photos")
    @Authenticated
    suspend fun getPhotos(
        @Query("page") page: Int,
        @Query("per_page") perSize: Int,
    ): Response<List<PhotoDto>>

}
