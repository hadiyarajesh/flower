package com.example.xml_app.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.xml_app.database.entity.Image
import kotlinx.coroutines.flow.Flow

@Dao
interface ImageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllImages(images: List<Image>)

    @Query("SELECT * FROM Image ORDER BY id")
    fun getAllImages(): Flow<List<Image>>

    @Query("SELECT * FROM Image WHERE id=:imageId ORDER BY id")
    fun getImageById(imageId: Long): Flow<Image>

    @Query("DELETE FROM Image")
    suspend fun deleteAllImages()
}