package com.hadiyarajesh.compose_app.di

import android.content.Context
import androidx.room.Room
import com.hadiyarajesh.compose_app.R
import com.hadiyarajesh.compose_app.database.FlowerDatabase
import com.hadiyarajesh.compose_app.database.dao.ImageDao
import com.hadiyarajesh.compose_app.database.dao.ImageRemoteKeyDao
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

    @Provides
    @Singleton
    fun provideImageRemoteKeyDao(db: FlowerDatabase): ImageRemoteKeyDao = db.imageRemoteKeyDao
}
