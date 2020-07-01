package com.hadiyarajesh.flowersample.di

import com.hadiyarajesh.flowersample.data.database.QuoteDatabase
import org.koin.dsl.module

val databaseModule = module {
    single { QuoteDatabase(context = get()) }
    single { QuoteDatabase(context = get()).quoteDao() }
}