package com.hadiyarajesh.flowersample.di

import android.content.Context
import androidx.room.Room
import com.hadiyarajesh.flowersample.R
import com.hadiyarajesh.flowersample.data.database.QuoteDatabase
import com.hadiyarajesh.flowersample.data.database.dao.QuoteDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideQuoteDatabase(@ApplicationContext context: Context): QuoteDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            QuoteDatabase::class.java,
            "${context.getString(R.string.quote)}.db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideQuoteDao(db: QuoteDatabase): QuoteDao = db.quoteDao
}
