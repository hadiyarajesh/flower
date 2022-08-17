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

package com.hadiyarajesh.flower_core

data class Resource<out T>(
    val status: Status<T>
) {
    sealed class Status<out T> {
        data class Loading<out T>(val data: T?) : Status<T>()

        data class Success<out T>(val data: T & Any) : Status<T & Any>()

        class EmptySuccess : Status<Nothing>()

        data class Error<out T>(
            val message: String,
            val statusCode: Int,
            val data: T?
        ) : Status<T>()
    }

    companion object {
        fun <T> loading(data: T? = null): Resource<T> {
            return Resource(status = Status.Loading(data))
        }

        fun <T> success(data: T & Any): Resource<T> {
            return Resource(status = Status.Success(data))
        }

        fun <T> emptySuccess(): Resource<T> = Resource(status = Status.EmptySuccess())

        fun <T> error(msg: String, statusCode: Int, data: T?): Resource<T> {
            return Resource(status = Status.Error(msg, statusCode, data))
        }
    }
}
