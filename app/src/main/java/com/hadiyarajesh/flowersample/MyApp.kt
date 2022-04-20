package com.hadiyarajesh.flowersample

import android.app.Application
import com.hadiyarajesh.flowersample.di.databaseModule
import com.hadiyarajesh.flowersample.di.networkModule
import com.hadiyarajesh.flowersample.di.repositoryModule
import com.hadiyarajesh.flowersample.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class MyApp: Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@MyApp)
            modules(listOf(databaseModule, networkModule, repositoryModule, viewModelModule))
        }
    }
}
