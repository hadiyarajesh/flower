package com.hadiyarajesh.flowersample.data.network

import com.hadiyarajesh.flower.ApiResponse
import com.hadiyarajesh.flowersample.data.database.entity.Quote
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {

    @GET("posts/?orderby=id&per_page=1")
    fun getRandomQuote(@Query("page") pageNo: Int): Flow<ApiResponse<List<Quote>>>
}