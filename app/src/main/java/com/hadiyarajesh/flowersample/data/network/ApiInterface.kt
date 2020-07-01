package com.hadiyarajesh.flowersample.data.network

import com.hadiyarajesh.flower.ApiResponse
import com.hadiyarajesh.flowersample.data.database.entity.Quote
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET

interface ApiInterface {
    
    @GET("quotes/random")
    fun getRandomQuote(): Flow<ApiResponse<Quote>>
}