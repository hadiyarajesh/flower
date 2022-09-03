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

package com.hadiyarajesh.flower_retrofit

import com.hadiyarajesh.flower_core.ApiResponse
import com.hadiyarajesh.flower_retrofit.common.toCommonResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ResultCall<T>(
    proxy: Call<T>
) : CallDelegate<T, ApiResponse<T>>(proxy) {
    override fun enqueueImplementation(callback: Callback<ApiResponse<T>>) =
        proxy.enqueue(
            object : Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    callback.onResponse(
                        this@ResultCall,
                        Response.success(ApiResponse.create(response.toCommonResponse()))
                    )
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    callback.onResponse(this@ResultCall, Response.success(ApiResponse.create(t)))
                }
            }
        )

    override fun cloneImplementation(): Call<ApiResponse<T>> = ResultCall(proxy.clone())
}
