package com.hadiyarajesh.flowersample.di

import com.hadiyarajesh.flower.calladpater.FlowCallAdapterFactory
import com.hadiyarajesh.flowersample.data.network.ApiInterface
import com.hadiyarajesh.flowersample.data.network.QuoteAdapter
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

val networkModule = module {
    val apiURL = "https://quotesondesign.com/wp-json/wp/v2/"

    val okHttpClient = OkHttpClient.Builder()
        .followRedirects(true)
        .followSslRedirects(true)
        .retryOnConnectionFailure(true)
        .build()

    val customMoshi = Moshi.Builder()
            .add(QuoteAdapter())
            .build()

    val retrofit = Retrofit.Builder()
        .baseUrl(apiURL)
        .client(okHttpClient)
        .addCallAdapterFactory(FlowCallAdapterFactory.create())
        .addConverterFactory(MoshiConverterFactory.create(customMoshi))
        .build()
        .create(ApiInterface::class.java)

    single<ApiInterface> { retrofit }
}
