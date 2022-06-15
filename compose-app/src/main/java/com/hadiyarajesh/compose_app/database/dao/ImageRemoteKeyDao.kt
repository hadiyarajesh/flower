package com.hadiyarajesh.compose_app.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hadiyarajesh.compose_app.database.entity.ImageRemoteKey

@Dao
interface ImageRemoteKeyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllKeys(keys: List<ImageRemoteKey>)

    @Query("SELECT * FROM ImageRemoteKey ORDER BY imageId DESC")
    fun getAllKeysByDesc(): List<ImageRemoteKey>

    @Query("DELETE FROM ImageRemoteKey")
    fun deleteAllKeys()
}
