package com.hadiyarajesh.flower_ktorfit.common

import com.hadiyarajesh.flower_core.implement.Response
import io.ktor.client.call.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.reflect.*

internal fun <T> HttpResponse.toCommonResponse(typeInfo: TypeInfo): Response<T> {
    return object : Response<T> {
        override val isSuccessful: Boolean
            get() = this@toCommonResponse.status.isSuccess()

        override val code: Int
            get() = this@toCommonResponse.status.value

        override val description: String
            get() = this@toCommonResponse.status.description

        override suspend fun body(): T? {
            return this@toCommonResponse.body(typeInfo)
        }

        override fun headers(): Set<Map.Entry<String, List<String>>> {
            return this@toCommonResponse.headers.entries()
        }
    }
}