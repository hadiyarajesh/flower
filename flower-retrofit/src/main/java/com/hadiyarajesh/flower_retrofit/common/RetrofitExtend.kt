package com.hadiyarajesh.flower_retrofit.common

import com.hadiyarajesh.flower_core.implement.Response

internal fun <T> retrofit2.Response<T>.toCommonResponse(): Response<T> {
    return object : Response<T> {
        override val isSuccessful: Boolean
            get() = this@toCommonResponse.isSuccessful

        override val code: Int
            get() = this@toCommonResponse.code()

        override val description: String
            get() = this@toCommonResponse.errorBody()?.string() ?: this@toCommonResponse.message()

        override fun body(): T? {
            return this@toCommonResponse.body()
        }

        override fun headers(): Set<Map.Entry<String, List<String>>> {
            return this@toCommonResponse.headers().toMultimap().entries
        }
    }
}