package com.hadiyarajesh.flowersample.data.repository

import android.util.Log
import com.hadiyarajesh.flower_core.Resource
import com.hadiyarajesh.flower_core.dbBoundResource
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
        return dbBoundResource(
                fetchFromLocal = {
                    Log.i(TAG, "Fetching from local cache")
                    val localResult = quoteDao.getQuote(pageNo)
                    localResult
                },
                shouldMakeNetworkRequest = {
                    Log.i(TAG, "Checking if remote fetch is needed")
                    it == null
                },
                makeNetworkRequest = {
                    Log.i(TAG, "Fetching from remote server")
                    quoteApi.getRandomQuote(pageNo)
                },
                processRequestResponse = {},
                saveRequestData = { quotes ->
                    Log.i(TAG, "Saving from remote data to local cache")
                    val copiedQuotes = quotes.map { it.copy(primaryId = pageNo) }
                    copiedQuotes.forEach { quoteDao.insertOrUpdateQuote(it) }
                },
                onRequestFailed = { errorBody, statusCode -> onFailed(errorBody, statusCode) },
        ).map {
            when (it.status) {
                is Resource.Status.LOADING -> {
                    Resource.loading(null)
                }
                is Resource.Status.SUCCESS -> {
                    val quote = (it.status as Resource.Status.SUCCESS).data
                    Resource.success(quote)
                }
                is Resource.Status.ERROR -> {
                    val error = it.status as Resource.Status.ERROR
                    Resource.error(error.message, error.statusCode, error.data)
                }
                is Resource.Status.EMPTY -> {
                    Resource.empty()
                }
            }
        }.flowOn(Dispatchers.IO)
    }
}
