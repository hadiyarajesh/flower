package com.hadiyarajesh.flower

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

/**
 * It will handle performing network request, getting result of it and caching it to local database
 * @param fetchFromLocal - Retrieve data from local database
 * @param shouldFetchFromRemote - Whether or not make network request
 * @param fetchFromRemote - Retrieve data from network request
 * @param processRemoteResponse - process result of network request (e.g., save response headers)
 * @param saveRemoteData - Save result of network request
 * @param onFetchFailed - Perform action when network request fails
 */
@ExperimentalCoroutinesApi
inline fun <DB, REMOTE> networkBoundResource(
    crossinline fetchFromLocal: suspend () -> Flow<DB>,
    crossinline shouldFetchFromRemote: suspend (DB?) -> Boolean = { true },
    crossinline fetchFromRemote: suspend () -> Flow<ApiResponse<REMOTE>>,
    crossinline processRemoteResponse: (response: ApiSuccessResponse<REMOTE>) -> Unit = { },
    crossinline saveRemoteData: suspend (REMOTE) -> Unit = { },
    crossinline onFetchFailed: (errorBody: String?, statusCode: Int) -> Unit = { _: String?, _: Int -> }
) = flow<Resource<DB>> {
    emit(Resource.loading(null))
    val localData = fetchFromLocal().first()

    if (shouldFetchFromRemote(localData)) {
        emit(Resource.loading(localData))
        fetchFromRemote().collect { apiResponse ->
            when (apiResponse) {
                is ApiSuccessResponse -> {
                    processRemoteResponse(apiResponse)
                    apiResponse.body?.let { saveRemoteData(it) }
                    emitAll(fetchFromLocal().map { dbData ->
                        Resource.success(dbData)
                    })
                }
                is ApiErrorResponse -> {
                    onFetchFailed(apiResponse.errorMessage, apiResponse.statusCode)
                    emitAll(fetchFromLocal().map { dbData ->
                        Resource.error(
                            apiResponse.errorMessage,
                            dbData
                        )
                    })
                }
                is ApiEmptyResponse -> {
                    emit(Resource.success(null))
                }
            }
        }
    } else {
        emitAll(fetchFromLocal().map { Resource.success(it) })
    }
}