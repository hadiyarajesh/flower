package com.hadiyarajesh.flower_core

import com.hadiyarajesh.flower_core.implement.Response

sealed class ApiResponse<T> {
    companion object {
        fun <T> create(error: Throwable): ApiErrorResponse<T> {
            return ApiErrorResponse(errorMessage = error.message ?: "Unknown error", statusCode = 0)
        }

        fun <T> create(response: Response<T>): ApiResponse<T> {
            return if (response.isSuccessful) {
                val body = response.body()
                val headers = response.headers()
                if (body == null || response.code == 204) {
                    ApiEmptyResponse()
                } else {
                    ApiSuccessResponse(body, headers)
                }
            } else {
                val msg = response.description
                val errorMsg = if (msg.isNullOrEmpty()) {
                    "Unknown"
                } else {
                    msg
                }
                ApiErrorResponse(
                    errorMessage = errorMsg,
                    statusCode = response.code
                )
            }
        }
    }
}

/**
 * Separate class for HTTP 204 responses so that we can make ApiSuccessResponse's body non-null.
 */
class ApiEmptyResponse<T> : ApiResponse<T>()

data class ApiSuccessResponse<T>(
    val body: T?,
    val headers: Set<Map.Entry<String, List<String>>>
) : ApiResponse<T>()

data class ApiErrorResponse<T>(val errorMessage: String, val statusCode: Int) : ApiResponse<T>()
