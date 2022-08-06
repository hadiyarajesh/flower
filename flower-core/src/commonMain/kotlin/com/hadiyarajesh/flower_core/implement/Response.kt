package com.hadiyarajesh.flower_core.implement

interface Response<T> {
    val isSuccessful: Boolean
    val code: Int
    val description: String

    suspend fun body(): T?
    fun headers(): Set<Map.Entry<String, List<String>>>
}