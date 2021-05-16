package com.hadiyarajesh.flowersample.data.repository

import android.util.Log
import com.hadiyarajesh.flower.*
import com.hadiyarajesh.flowersample.data.database.dao.QuoteDao
import com.hadiyarajesh.flowersample.data.database.entity.Quote
import com.hadiyarajesh.flowersample.data.network.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class QuoteRepository(
        private val apiInterface: ApiInterface,
        private val quoteDao: QuoteDao
) {

    companion object {
        const val TAG = "Repository"
    }

    @ExperimentalCoroutinesApi
    fun getRandomQuote(pageNo: Int, onFailed: (String?,Int) -> Unit = { _: String?, _: Int -> }): Flow<Resource<Quote>> {
        val networkBoundFlow = networkBoundResource(
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
                    apiInterface.getRandomQuote(pageNo)
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
        }
        return networkBoundFlow.flowOn(Dispatchers.IO)
    }
}