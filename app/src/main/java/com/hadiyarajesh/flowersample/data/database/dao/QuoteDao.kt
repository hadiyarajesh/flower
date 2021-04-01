package com.hadiyarajesh.flowersample.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hadiyarajesh.flowersample.data.database.entity.Quote
import kotlinx.coroutines.flow.Flow

@Dao
interface QuoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdateQuote(quote: Quote)

    @Query("SELECT * from quote where pageId = :pageNo")
    fun getQuote(pageNo: Int): Flow<Quote>
}