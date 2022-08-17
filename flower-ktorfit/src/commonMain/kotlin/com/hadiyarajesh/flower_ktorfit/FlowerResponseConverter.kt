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

package com.hadiyarajesh.flower_ktorfit

import com.hadiyarajesh.flower_core.ApiResponse
import com.hadiyarajesh.flower_ktorfit.common.toCommonResponse
import de.jensklingenberg.ktorfit.adapter.ResponseConverter
import io.ktor.client.statement.*
import io.ktor.util.reflect.*
import kotlinx.coroutines.flow.flow
import kotlin.reflect.KClass

class FlowerResponseConverter : ResponseConverter {
    override fun supportedType(returnTypeName: String): Boolean {
        return returnTypeName == "kotlinx.coroutines.flow.Flow"
    }

    override fun <PRequest : Any> wrapResponse(
        returnTypeName: String,
        requestFunction: suspend () -> Pair<TypeInfo, HttpResponse>
    ): Any {
        return flow<ApiResponse<Any>> {
            try {
                val (info, response) = requestFunction()

                val kotlinType = info.kotlinType
                    ?: throw IllegalArgumentException("Type must match Flow<ApiResponse<YourModel>>")
                val modelKTypeProjection =
                    if (kotlinType.arguments.size >= 0) kotlinType.arguments[0] else throw IllegalArgumentException(
                        "Type must match Flow<ApiResponse<YourModel>>"
                    )
                val modelKType = modelKTypeProjection.type
                    ?: throw IllegalArgumentException("Could not get a KType of your model class or return type")
                val modelClass = (modelKType.classifier as? KClass<*>?)
                    ?: throw IllegalArgumentException("Could not parse your model class or return type to a KClass")

                emit(
                    ApiResponse.create(
                        response.toCommonResponse(
                            TypeInfo(
                                modelClass,
                                modelKType.platformType,
                                modelKType
                            )
                        )
                    )
                )
            } catch (e: Throwable) {
                emit(ApiResponse.create(e))
            }
        }
    }
}
