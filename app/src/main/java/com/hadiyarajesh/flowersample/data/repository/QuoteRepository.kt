/*
 *  Copyright (C) 2022 Rajesh Hadiya
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

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

    fun getRandomQuote(
        pageNo: Int,
        onFailed: (String?, Int) -> Unit = { _: String?, _: Int -> }
    ): Flow<Resource<Quote>> {
        return dbBoundResource(
            fetchFromLocal = {
                Log.i(TAG, "Fetching from local cache")
                quoteDao.getQuote(pageNo)
            },
            shouldMakeNetworkRequest = {
                Log.i(TAG, "Checking if remote fetch is needed")
                it == null
            },
            makeNetworkRequest = {
                Log.i(TAG, "Fetching from remote server")
                quoteApi.getRandomQuote(pageNo)
            },
            processNetworkResponse = {},
            saveResponseData = { quotes ->
                Log.i(TAG, "Saving from remote data to local cache")
                val copiedQuotes = quotes.map { it.copy(primaryId = pageNo) }
                copiedQuotes.forEach { quoteDao.insertOrUpdateQuote(it) }
            },
            onNetworkRequestFailed = { errorMessage, httpStatusCode ->
                onFailed(errorMessage, httpStatusCode)
            },
        ).map {
            when (it.status) {
                is Resource.Status.Loading -> {
                    Resource.loading(null)
                }

                is Resource.Status.Success -> {
                    val quote = (it.status as Resource.Status.Success).data
                    Resource.success(quote)
                }

                is Resource.Status.EmptySuccess -> {
                    Resource.emptySuccess()
                }

                is Resource.Status.Error -> {
                    val error = it.status as Resource.Status.Error
                    Resource.error(error.errorMessage, error.httpStatusCode, error.data)
                }
            }
        }.flowOn(Dispatchers.IO)
    }
}
