package com.hadiyarajesh.flowersample.data.network

import com.hadiyarajesh.flower_core.ApiResponse
import com.hadiyarajesh.flowersample.data.database.entity.Quote
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET
import retrofit2.http.Query

interface QuoteApi {
    @GET("posts/?orderby=id&per_page=1")
    @de.jensklingenberg.ktorfit.http.GET("posts/?orderby=id&per_page=1")
    fun getRandomQuote(@Query("page") @de.jensklingenberg.ktorfit.http.Query("page") pageNo: Int): Flow<ApiResponse<List<Quote>>>
}
