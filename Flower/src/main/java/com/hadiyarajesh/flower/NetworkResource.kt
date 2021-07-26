package com.hadiyarajesh.flower

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.collect

/**
 * It will handle performing network request and getting result of it
 * @param fetchFromRemote - Retrieve data from network request
 * @param onFetchFailed - Perform action when network request fails
 */
inline fun <REMOTE> networkResource(
    crossinline fetchFromRemote: suspend () -> Flow<ApiResponse<REMOTE>>,
    crossinline onFetchFailed: (errorBody: String?, statusCode: Int) -> Unit = { _: String?, _: Int -> }
) = flow<Resource<REMOTE>> {
    emit(Resource.loading(null))

    fetchFromRemote().collect { apiResponse ->
        when (apiResponse) {
            is ApiSuccessResponse -> {
                apiResponse.body?.let {
                    emit(Resource.success(it))
                }
            }
            is ApiErrorResponse -> {
                onFetchFailed(apiResponse.errorMessage, apiResponse.statusCode)
                emit(Resource.error(apiResponse.errorMessage, null))
            }
            is ApiEmptyResponse -> {
                emit(Resource.success(null))
            }
        }
    }
}