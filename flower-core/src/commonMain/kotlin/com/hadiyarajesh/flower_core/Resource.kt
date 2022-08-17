package com.hadiyarajesh.flower_core

data class Resource<out T>(
    val status: Status<T>
) {

    sealed class Status<out T> {

        data class Success<out T>(val data: T & Any) : Status<T & Any>()

        data class Error<out T>(
            val message: String,
            val statusCode: Int,
            val data: T?
        ) : Status<T>()

        data class Loading<out T>(val data: T?) : Status<T>()

        class EmptySuccess : Status<Nothing>()
    }
    companion object {
        fun <T> success(data: T & Any): Resource<T> {
            return Resource(status = Status.Success(data))
        }

        fun <T> error(msg: String, statusCode: Int, data: T?): Resource<T> {
            return Resource(status = Status.Error(msg, statusCode, data))
        }

        fun <T> loading(data: T? = null): Resource<T> {
            return Resource(status = Status.Loading(data))
        }

        fun <T> empty(): Resource<T> = Resource(status = Status.EmptySuccess())
    }
}

