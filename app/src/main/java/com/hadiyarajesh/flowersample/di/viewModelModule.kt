package com.hadiyarajesh.flowersample.di

import com.hadiyarajesh.flowersample.ui.MainActivityViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { MainActivityViewModel(quoteRepository = get()) }
}