package com.hadiyarajesh.flower_retrofit

import com.hadiyarajesh.flower_core.ApiResponse
import com.hadiyarajesh.flower_retrofit.common.toCommonResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.awaitResponse
import java.lang.reflect.Type

class FlowerCallAdapter(
    private val responseType: Type
) : CallAdapter<Type, Flow<ApiResponse<Type>>> {
    override fun responseType() = responseType

    override fun adapt(call: Call<Type>): Flow<ApiResponse<Type>> = flow {
        val response = call.awaitResponse()
        emit(ApiResponse.create(response.toCommonResponse()))
    }.catch { error ->
        emit(ApiResponse.create(error))
    }
}