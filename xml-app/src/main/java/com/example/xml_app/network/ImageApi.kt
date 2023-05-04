package com.example.xml_app.network

import com.example.xml_app.database.entity.Image
import com.hadiyarajesh.flower_core.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface ImageApi {
    @GET("v2/list")
    suspend fun getAllImages(
        @retrofit2.http.Query("page") page: Int,
        @retrofit2.http.Query("size") size: Int = 50
    ): ApiResponse<List<Image>>

    @GET("id/{imageId}/info")
    suspend fun getImage(@Path("imageId") imageId: Long): ApiResponse<Image>
}