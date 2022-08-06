package com.hadiyarajesh.compose_app.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ImageRemoteKey(
    @PrimaryKey
    val imageId: Long,
    val prevKey: Int?,
    val nextKey: Int?
)
