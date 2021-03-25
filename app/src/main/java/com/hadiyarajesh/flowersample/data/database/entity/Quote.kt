package com.hadiyarajesh.flowersample.data.database.entity


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Entity(tableName = "quote")
@JsonClass(generateAdapter = true)
data class Quote(
    @ColumnInfo(name = "_internalId")
    @Json(name = "id")
    val _internalId: Int,
    @PrimaryKey(autoGenerate = false)
    val pageId: Int,
    @Json(name = "title")
    val author: String,
    val quote: String,
    @Json(name = "author")
    val authorId: Int
)