package com.hadiyarajesh.flower.calladpater

import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

abstract class CallDelegate<TIn, TOut>(
    protected val proxy: Call<TIn>
) : Call<TOut> {
    override fun execute(): Response<TOut> = throw NotImplementedError()
    override fun enqueue(callback: Callback<TOut>) = enqueueImplementation(callback)
    override fun clone(): Call<TOut> = cloneImplementation()

    override fun cancel() = proxy.cancel()
    override fun request(): Request = proxy.request()
    override fun timeout(): Timeout = proxy.timeout()
    override fun isExecuted(): Boolean = proxy.isExecuted
    override fun isCanceled(): Boolean = proxy.isCanceled

    abstract fun enqueueImplementation(callback: Callback<TOut>)
    abstract fun cloneImplementation(): Call<TOut>
}
