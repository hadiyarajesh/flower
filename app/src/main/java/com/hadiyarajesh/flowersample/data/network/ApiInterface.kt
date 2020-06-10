package com.hadiyarajesh.flowersample.data.network

import com.hadiyarajesh.flower.ApiResponse
import com.hadiyarajesh.flower.calladpater.FlowCallAdapterFactory
import com.hadiyarajesh.flowersample.data.database.entity.Quote
import kotlinx.coroutines.flow.Flow
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

interface ApiInterface {

    companion object {

        private const val API_URL = "https://programming-quotes-api.herokuapp.com/"

        operator fun invoke(): ApiInterface {

            val okHttpClient = OkHttpClient.Builder()
                .followRedirects(true)
                .followSslRedirects(true)
                .retryOnConnectionFailure(true)
                .build()

            return Retrofit.Builder()
                .baseUrl(API_URL)
                .client(okHttpClient)
                .addCallAdapterFactory(FlowCallAdapterFactory.create())
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
                .create(ApiInterface::class.java)
        }
    }


    @GET("quotes/random")
    fun getRandomQuote(): Flow<ApiResponse<Quote>>
}