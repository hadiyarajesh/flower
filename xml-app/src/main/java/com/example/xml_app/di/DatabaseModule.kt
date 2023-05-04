package com.example.xml_app.di

import android.content.Context
import androidx.room.Room
import com.example.xml_app.R
import com.example.xml_app.database.FlowerDatabase
import com.example.xml_app.database.dao.ImageDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Provides
    @Singleton
    fun provideFlowerDatabase(@ApplicationContext context: Context): FlowerDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            FlowerDatabase::class.java,
            "${context.getString(R.string.app_name)}.db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideImageDao(db: FlowerDatabase): ImageDao = db.imageDao
}