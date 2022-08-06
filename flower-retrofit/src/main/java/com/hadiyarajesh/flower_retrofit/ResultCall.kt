package com.hadiyarajesh.flower_retrofit

import com.hadiyarajesh.flower_core.ApiResponse
import com.hadiyarajesh.flower_retrofit.common.toCommonResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ResultCall<T>(
    proxy: Call<T>,
    private val scope: CoroutineScope
) : CallDelegate<T, ApiResponse<T>>(proxy) {
    override fun enqueueImplementation(callback: Callback<ApiResponse<T>>) =
        proxy.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                scope.launch(Dispatchers.IO) {
                    callback.onResponse(this@ResultCall, Response.success(ApiResponse.create(response.toCommonResponse())))
                }
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                callback.onResponse(this@ResultCall, Response.success(ApiResponse.create(t)))
            }
        })

    override fun cloneImplementation(): Call<ApiResponse<T>> = ResultCall(proxy.clone(), scope)
}