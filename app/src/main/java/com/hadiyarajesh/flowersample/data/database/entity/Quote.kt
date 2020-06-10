package com.hadiyarajesh.flowersample.data.database.entity


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

const val ID = 0

@Entity(tableName = "quote")
@JsonClass(generateAdapter = true)
data class Quote(
    @ColumnInfo(name = "_internalId")
    @Json(name = "_id")
    val _internalId: String,
    @ColumnInfo(name = "quote_id")
    val id: String,
    @Json(name = "en")
    val title: String,
    val author: String
) {
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    var primaryId: Int = ID
}