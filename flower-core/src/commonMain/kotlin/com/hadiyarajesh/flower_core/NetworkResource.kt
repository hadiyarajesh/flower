package com.hadiyarajesh.flower_core

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * It will handle performing network request and getting result of it
 * @author Rajesh Hadiya
 * @param makeNetworkRequest - Retrieve data from network request
 * @param onRequestFailed - Perform action when network request fails
 */
inline fun <REMOTE> networkResource(
    crossinline makeNetworkRequest: suspend () -> Flow<ApiResponse<REMOTE>>,
    crossinline onRequestFailed: (errorBody: String?, statusCode: Int) -> Unit = { _: String?, _: Int -> }
) = flow<Resource<REMOTE>> {
    emit(Resource.loading(null))

    makeNetworkRequest().collect { apiResponse ->
        when (apiResponse) {
            is ApiSuccessResponse -> {
                apiResponse.body?.let {
                    emit(Resource.success(data = it))
                }
            }
            is ApiErrorResponse -> {
                onRequestFailed(apiResponse.errorMessage, apiResponse.statusCode)
                emit(Resource.error(msg = apiResponse.errorMessage, statusCode = apiResponse.statusCode, null))
            }
            is ApiEmptyResponse -> {
                emit(Resource.empty())
            }
        }
    }
}
