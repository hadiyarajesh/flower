package com.hadiyarajesh.flowersample.di

import com.hadiyarajesh.flower_ktorfit.FlowerResponseConverter
import com.hadiyarajesh.flower_retrofit.FlowerCallAdapterFactory
import com.hadiyarajesh.flowersample.data.network.QuoteAdapter
import com.hadiyarajesh.flowersample.data.network.QuoteApi
import com.hadiyarajesh.flowersample.extensions.moshi
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.create
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val API_BASE_URL = "https://quotesondesign.com/wp-json/wp/v2/"

    private val customMoshi = Moshi.Builder()
        .add(QuoteAdapter())
        .build()

    @Provides
    @Singleton
    fun getOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .followRedirects(true)
            .followSslRedirects(true)
            .retryOnConnectionFailure(true)
            .build()
    }

    @Provides
    @Singleton
    fun provideHttpClient(): HttpClient {
        return HttpClient(CIO) {
            install(ContentNegotiation) {
                moshi(customMoshi)
            }
        }
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .client(okHttpClient)
            .addCallAdapterFactory(FlowerCallAdapterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create(customMoshi))
            .build()
    }

    @Provides
    @Singleton
    fun provideKtorfit(client: HttpClient): Ktorfit {
        return Ktorfit(API_BASE_URL, client).addResponseConverter(FlowerResponseConverter())
    }

    /**
     * Either provide the API using Retrofit or using Ktorfit
     * DO NOT provide both at the same time
     */

    // @Provides
    // @Singleton
    // fun provideQuoteApi(retrofit: Retrofit): QuoteApi = retrofit.create(QuoteApi::class.java)

    @Provides
    @Singleton
    fun provideQuoteApi(ktorfit: Ktorfit): QuoteApi = ktorfit.create()
}
