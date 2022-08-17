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

package com.hadiyarajesh.flower_ktorfit.common

import com.hadiyarajesh.flower_core.implement.Response
import io.ktor.client.call.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.reflect.*

internal suspend fun <T> HttpResponse.toCommonResponse(typeInfo: TypeInfo): Response<T> {
    val responseBody: T? = this.body(typeInfo)

    return object : Response<T> {
        override val isSuccessful: Boolean
            get() = this@toCommonResponse.status.isSuccess()

        override val code: Int
            get() = this@toCommonResponse.status.value

        override val description: String
            get() = this@toCommonResponse.status.description

        override fun body(): T? {
            return responseBody
        }

        override fun headers(): Set<Map.Entry<String, List<String>>> {
            return this@toCommonResponse.headers.entries()
        }
    }
}
