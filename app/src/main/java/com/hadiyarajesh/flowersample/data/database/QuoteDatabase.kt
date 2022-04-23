package com.hadiyarajesh.flowersample.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.hadiyarajesh.flowersample.data.database.dao.QuoteDao
import com.hadiyarajesh.flowersample.data.database.entity.Quote

@Database(entities = [Quote::class], version = 1, exportSchema = true)
abstract class QuoteDatabase : RoomDatabase() {
        abstract val quoteDao: QuoteDao
}
