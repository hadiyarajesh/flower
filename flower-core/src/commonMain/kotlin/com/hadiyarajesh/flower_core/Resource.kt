package com.hadiyarajesh.flower_core

data class Resource<out T>(
    val status: Status<T>
) {
    sealed class Status<out T> {
        data class SUCCESS<out T>(val data: T & Any) : Status<T & Any>()
        data class ERROR<out T>(
            val message: String,
            val statusCode: Int,
            val data: T?
        ) : Status<T>()
        data class LOADING<out T>(val data: T?) : Status<T>()
        class EMPTY<out T> : Status<T>()
    }

    companion object {
        fun <T> success(data: T & Any): Resource<T> {
            return Resource(status = Status.SUCCESS(data))
        }

        fun <T> error(msg: String, statusCode: Int, data: T?): Resource<T> {
            return Resource(status = Status.ERROR(msg, statusCode, data))
        }

        fun <T> loading(data: T? = null): Resource<T> {
            return Resource(status = Status.LOADING(data))
        }

        fun <T> empty(): Resource<T> = Resource(status = Status.EMPTY())
    }
}
