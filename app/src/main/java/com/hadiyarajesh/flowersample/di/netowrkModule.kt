package com.hadiyarajesh.flowersample.di

import com.hadiyarajesh.flower.calladpater.FlowCallAdapterFactory
import com.hadiyarajesh.flowersample.data.network.ApiInterface
import okhttp3.OkHttpClient
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

val networkModule = module {

    val apiURL = "https://programming-quotes-api.herokuapp.com/"

    val okHttpClient = OkHttpClient.Builder()
        .followRedirects(true)
        .followSslRedirects(true)
        .retryOnConnectionFailure(true)
        .build()

    val retrofit = Retrofit.Builder()
        .baseUrl(apiURL)
        .client(okHttpClient)
        .addCallAdapterFactory(FlowCallAdapterFactory.create())
        .addConverterFactory(MoshiConverterFactory.create())
        .build()
        .create(ApiInterface::class.java)

    single<ApiInterface> { retrofit }
}