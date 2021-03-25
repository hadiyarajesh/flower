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
    fun getRandomQuote(pageNo: Int): Flow<Resource<Quote>> {
        return networkBoundResource(
            fetchFromLocal = {
                quoteDao.getQuote(pageNo)
            },
            shouldFetchFromRemote = {
                it == null
            },
            fetchFromRemote = {
                apiInterface.getRandomQuote(pageNo)
            },
            processRemoteResponse = { response ->
                response.body?.map { it.copy(pageId = pageNo) }
            },
            saveRemoteData = { quotes ->
                quotes.forEach { quoteDao.insertOrUpdateQuote(it) }
            },
            onFetchFailed = { _, _ -> }
        ).flowOn(Dispatchers.IO)
    }
}