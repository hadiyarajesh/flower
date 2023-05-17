package com.example.xml_app.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.xml_app.database.entity.Image
import com.example.xml_app.database.dao.ImageDao

@Database(
    version = 1,
    exportSchema = true,
    entities = [Image::class]
)
abstract class FlowerDatabase : RoomDatabase() {
    abstract val imageDao: ImageDao
}
