package com.hadiyarajesh.flower

data class Resource<out T>(
    val status: Status,
    val data: T?
) {
    sealed class Status {
        object SUCCESS : Status()
        data class ERROR(
            val message: String,
            val statusCode: Int
        ) : Status()
        object LOADING : Status()
    }

    companion object {
        fun <T> success(data: T?): Resource<T> {
            return Resource(status = Status.SUCCESS, data = data)
        }

        fun <T> error(msg: String, statusCode: Int, data: T? = null): Resource<T> {
            return Resource(status = Status.ERROR(msg, statusCode), data = data)
        }

        fun <T> loading(data: T? = null): Resource<T> {
            return Resource(status = Status.LOADING, data = data)
        }
    }
}
