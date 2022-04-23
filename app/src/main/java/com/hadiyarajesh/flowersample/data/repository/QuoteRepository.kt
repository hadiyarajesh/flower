package com.hadiyarajesh.flowersample.data.repository

import android.util.Log
import com.hadiyarajesh.flower.Resource
import com.hadiyarajesh.flower.networkBoundResource
import com.hadiyarajesh.flowersample.data.database.dao.QuoteDao
import com.hadiyarajesh.flowersample.data.database.entity.Quote
import com.hadiyarajesh.flowersample.data.network.QuoteApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class QuoteRepository @Inject constructor(
    private val quoteApi: QuoteApi,
    private val quoteDao: QuoteDao
) {
    companion object {
        const val TAG = "Repository"
    }

    fun getRandomQuote(pageNo: Int, onFailed: (String?,Int) -> Unit = { _: String?, _: Int -> }): Flow<Resource<Quote>> {
        return networkBoundResource(
                fetchFromLocal = {
                    Log.i(TAG, "Fetching from local cache")
                    val localResult = quoteDao.getQuote(pageNo)
                    localResult
                },
                shouldFetchFromRemote = {
                    Log.i(TAG, "Checking if remote fetch is needed")
                    it == null
                },
                fetchFromRemote = {
                    Log.i(TAG, "Fetching from remote server")
                    quoteApi.getRandomQuote(pageNo)
                },
                processRemoteResponse = {},
                saveRemoteData = { quotes ->
                    Log.i(TAG, "Saving from remote data to local cache")
                    val copiedQuotes = quotes.map { it.copy(primaryId = pageNo) }
                    copiedQuotes.forEach { quoteDao.insertOrUpdateQuote(it) }
                },
                onFetchFailed = { errorBody, statusCode -> onFailed(errorBody, statusCode) },
        ).map {
            when (it.status) {
                Resource.Status.LOADING -> {
                    Resource.loading(null)
                }
                Resource.Status.SUCCESS -> {
                    val quote = it.data
                    Resource.success(quote)
                }
                Resource.Status.ERROR -> {
                    Resource.error(it.message!!, null)
                }
            }
        }.flowOn(Dispatchers.IO)
    }
}
