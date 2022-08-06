package com.hadiyarajesh.compose_app.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.hadiyarajesh.compose_app.database.dao.ImageDao
import com.hadiyarajesh.compose_app.database.dao.ImageRemoteKeyDao
import com.hadiyarajesh.compose_app.database.entity.Image
import com.hadiyarajesh.compose_app.database.entity.ImageRemoteKey

@Database(
    version = 1,
    exportSchema = true,
    entities = [Image::class, ImageRemoteKey::class]
)
abstract class FlowerDatabase : RoomDatabase() {
    abstract val imageDao: ImageDao
    abstract val imageRemoteKeyDao: ImageRemoteKeyDao
}
