package com.hadiyarajesh.flower_core

import kotlinx.coroutines.flow.*

/**
 * It will handle performing network request, getting result of it and caching it to local database
 * @author Rajesh Hadiya
 * @param fetchFromLocal - Retrieve data from local database
 * @param shouldMakeNetworkRequest - Whether or not make network request
 * @param makeNetworkRequest - Retrieve data from network request
 * @param processRequestResponse - process result of network request (e.g., save response headers)
 * @param saveRequestData - Save result of network request
 * @param onRequestFailed - Perform action when network request fails
 */
inline fun <DB, REMOTE> dbBoundResource(
    crossinline fetchFromLocal: suspend () -> Flow<DB>,
    crossinline shouldMakeNetworkRequest: suspend (DB?) -> Boolean = { true },
    crossinline makeNetworkRequest: suspend () -> Flow<ApiResponse<REMOTE>>,
    crossinline processRequestResponse: (response: ApiSuccessResponse<REMOTE>) -> Unit = { },
    crossinline saveRequestData: suspend (REMOTE) -> Unit = { },
    crossinline onRequestFailed: (errorBody: String?, statusCode: Int) -> Unit = { _: String?, _: Int -> }
) = flow<Resource<DB>> {
    emit(Resource.loading(null))
    val localData = fetchFromLocal().first()

    if (shouldMakeNetworkRequest(localData)) {
        emit(Resource.loading(localData))
        makeNetworkRequest().collect { apiResponse ->
            when (apiResponse) {
                is ApiSuccessResponse -> {
                    processRequestResponse(apiResponse)
                    apiResponse.body?.let { saveRequestData(it) }
                    emitAll(
                        fetchFromLocal().map { dbData ->
                            dbData?.let {
                                Resource.success(data = dbData)
                            } ?: Resource.empty()
                        }
                    )
                }
                is ApiErrorResponse -> {
                    onRequestFailed(apiResponse.errorMessage, apiResponse.statusCode)
                    emitAll(
                        fetchFromLocal().map { dbData ->
                            Resource.error(
                                msg = apiResponse.errorMessage,
                                statusCode = apiResponse.statusCode,
                                data = dbData
                            )
                        }
                    )
                }
                is ApiEmptyResponse -> {
                    emit(Resource.empty())
                }
            }
        }
    } else {
        emitAll(fetchFromLocal().map { dbData ->
            dbData?.let {
                Resource.success(it)
            } ?: Resource.empty()
        })
    }
}
