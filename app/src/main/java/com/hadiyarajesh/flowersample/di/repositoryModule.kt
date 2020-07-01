package com.hadiyarajesh.flowersample.di

import com.hadiyarajesh.flowersample.data.repository.QuoteRepository
import org.koin.dsl.module

val repositoryModule = module {
    single { QuoteRepository(apiInterface = get(), quoteDao = get()) }
}