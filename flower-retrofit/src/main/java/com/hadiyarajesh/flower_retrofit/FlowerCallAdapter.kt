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
