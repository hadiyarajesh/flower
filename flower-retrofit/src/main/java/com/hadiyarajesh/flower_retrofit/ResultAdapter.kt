package com.hadiyarajesh.flower_retrofit

import com.hadiyarajesh.flower_core.ApiResponse
import kotlinx.coroutines.CoroutineScope
import retrofit2.Call
import retrofit2.CallAdapter
import java.lang.reflect.Type

class ResultAdapter(
    private val type: Type,
    private val scope: CoroutineScope
) : CallAdapter<Type, Call<ApiResponse<Type>>> {
    override fun adapt(call: Call<Type>): Call<ApiResponse<Type>> = ResultCall(call, scope)
    override fun responseType(): Type = type
}