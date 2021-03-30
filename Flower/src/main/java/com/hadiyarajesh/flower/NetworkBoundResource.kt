package com.hadiyarajesh.flower

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

/**
 * This core function will handle database caching after performing network request
 */
@ExperimentalCoroutinesApi
inline fun <DB, REMOTE> networkBoundResource(
    crossinline fetchFromLocal: suspend () -> Flow<DB>,
    crossinline shouldFetchFromRemote: suspend (DB?) -> Boolean = { true },
    crossinline fetchFromRemote: suspend () -> Flow<ApiResponse<REMOTE>>,
    crossinline processRemoteResponse: (response: ApiSuccessResponse<REMOTE>) -> Unit = { Unit },
    crossinline saveRemoteData: suspend (REMOTE) -> Unit = { Unit },
    crossinline onFetchFailed: (errorBody: String?, statusCode: Int) -> Unit = { _: String?, _: Int -> Unit }
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
                    emitAll(fetchFromLocal().map {
                        Resource.error(
                            apiResponse.errorMessage,
                            it
                        )
                    })
                }

                else -> { }
            }
        }
    } else {
        emitAll(fetchFromLocal().map { Resource.success(it) })
    }
}