package com.hadiyarajesh.compose_app.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hadiyarajesh.compose_app.database.entity.Image

@Dao
interface ImageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllImages(images: List<Image>)

    @Query("SELECT * FROM Image ORDER BY id")
    fun getAllImages(): PagingSource<Int, Image>

    @Query("DELETE FROM Image")
    suspend fun deleteAllImages()
}
