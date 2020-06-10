package com.hadiyarajesh.flowersample.data.repository

import com.hadiyarajesh.flower.Resource
import com.hadiyarajesh.flower.networkBoundResource
import com.hadiyarajesh.flowersample.data.database.dao.QuoteDao
import com.hadiyarajesh.flowersample.data.database.entity.Quote
import com.hadiyarajesh.flowersample.data.network.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

class QuoteRepository(
    private val apiInterface: ApiInterface,
    private val quoteDao: QuoteDao
) {

    @ExperimentalCoroutinesApi
    fun getRandomQuote(): Flow<Resource<Quote>> {
        return networkBoundResource(
            fetchFromLocal = { quoteDao.getQuote() },
            shouldFetchFromRemote = { true },
            fetchFromRemote = { apiInterface.getRandomQuote() },
            processRemoteResponse = { },
            saveRemoteData = { quoteDao.insertOrUpdateQuote(it) },
            onFetchFailed = { _, _ -> }
        ).flowOn(Dispatchers.IO)
    }
}