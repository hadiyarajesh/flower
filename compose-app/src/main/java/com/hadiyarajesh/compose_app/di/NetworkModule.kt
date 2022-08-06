package com.hadiyarajesh.compose_app.di

import com.hadiyarajesh.compose_app.network.ImageApi
import com.hadiyarajesh.compose_app.utility.Constants
import com.hadiyarajesh.flower_ktorfit.FlowerResponseConverter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.create
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    @Singleton
    fun provideHttpClient(): HttpClient {
        return HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }
        }
    }

    @Provides
    @Singleton
    fun provideKtorfit(client: HttpClient): Ktorfit {
        return Ktorfit(baseUrl = Constants.API_BASE_URL, httpClient = client).addResponseConverter(FlowerResponseConverter())
    }

    @Provides
    @Singleton
    fun provideImageApi(ktorfit: Ktorfit): ImageApi = ktorfit.create<ImageApi>()
}
