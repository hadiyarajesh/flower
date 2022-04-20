package com.hadiyarajesh.flowersample.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.hadiyarajesh.flowersample.data.database.dao.QuoteDao
import com.hadiyarajesh.flowersample.data.database.entity.Quote

@Database(entities = [Quote::class], version = 1, exportSchema = true)
abstract class QuoteDatabase : RoomDatabase() {
    abstract fun quoteDao(): QuoteDao

    companion object {
        @Volatile
        private var instance: QuoteDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also { instance = it }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                QuoteDatabase::class.java,
                "quotes.db"
            ).build()
    }
}
