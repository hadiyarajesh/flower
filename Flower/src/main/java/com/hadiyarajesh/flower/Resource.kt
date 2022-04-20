package com.hadiyarajesh.flower

data class Resource<out T>(
    val status: Status,
    val data: T?,
    val message: String?
) {
    enum class Status {
        SUCCESS,
        ERROR,
        LOADING
    }

    companion object {
        fun <T> success(data: T?): Resource<T> {
            return Resource(status = Status.SUCCESS, data = data, message = null)
        }

        fun <T> error(msg: String, data: T? = null): Resource<T> {
            return Resource(status = Status.ERROR, data = data, message = msg)
        }

        fun <T> loading(data: T? = null): Resource<T> {
            return Resource(status = Status.LOADING, data = data, message = null)
        }
    }
}
