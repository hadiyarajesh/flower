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
import com.hadiyarajesh.flower_retrofit.internal.ResultAdapter
import kotlinx.coroutines.flow.Flow
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class FlowerCallAdapterFactory : CallAdapter.Factory() {
    override fun get(
        returnType: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? = when (getRawType(returnType)) {
        Flow::class.java -> {
            val observableType = getParameterUpperBound(0, returnType as ParameterizedType)
            require(observableType is ParameterizedType) { "resource must be parameterized" }
            val rawObservableType = getRawType(observableType)
            require(rawObservableType == ApiResponse::class.java) { "type must be a resource" }
            val bodyType = getParameterUpperBound(0, observableType)
            FlowerCallAdapter(bodyType)
        }

        Call::class.java -> {
            val callType = getParameterUpperBound(0, returnType as ParameterizedType)
            when (getRawType(callType)) {
                ApiResponse::class.java -> {
                    val resultType = getParameterUpperBound(0, callType as ParameterizedType)
                    ResultAdapter(resultType)
                }

                else -> null
            }
        }

        ApiResponse::class.java -> {
            val resultType = getParameterUpperBound(0, returnType as ParameterizedType)
            ResultAdapter(resultType)
        }

        else -> null
    }

    companion object {
        @JvmStatic
        fun create() = FlowerCallAdapterFactory()
    }
}
