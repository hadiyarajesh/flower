package com.hadiyarajesh.flowersample.di

import com.hadiyarajesh.flowersample.data.database.QuoteDatabase
import com.hadiyarajesh.flowersample.data.network.ApiInterface
import com.hadiyarajesh.flowersample.data.repository.QuoteRepository
import com.hadiyarajesh.flowersample.ui.MainActivityViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val databaseModule = module {
    single { QuoteDatabase(context = get()) }
    single { QuoteDatabase(context = get()).quoteDao() }
}

val networkModule = module {
    single { ApiInterface() }
}

val repositoryModule = module {
    single { QuoteRepository(apiInterface = get(), quoteDao = get()) }
}

val viewModelModule = module {
    viewModel { MainActivityViewModel(quoteRepository = get()) }
}