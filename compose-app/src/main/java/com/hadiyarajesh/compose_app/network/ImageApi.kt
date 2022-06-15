package com.hadiyarajesh.compose_app.network

import com.hadiyarajesh.compose_app.database.entity.Image
import retrofit2.http.GET
import retrofit2.http.Query

interface ImageApi {
    @GET("v2/list")
    suspend fun getAllImages(
        @Query("page") page: Int,
        @Query("size") size: Int = 30
    ): List<Image>
}
