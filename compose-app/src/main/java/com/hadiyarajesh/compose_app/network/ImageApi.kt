package com.hadiyarajesh.compose_app.network

import com.hadiyarajesh.compose_app.database.entity.Image
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Query

interface ImageApi {
    @GET("v2/list")
    fun getAllImages(@Query("page") page: Int, @Query("size") size: Int = 30): List<Image>
}
