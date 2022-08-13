package com.hadiyarajesh.flowersample.extensions

import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.serialization.*
import io.ktor.util.*
import io.ktor.util.reflect.*
import io.ktor.utils.io.*
import io.ktor.utils.io.charsets.*

class MoshiConverter(
    private val moshi: Moshi
): ContentConverter {
    @OptIn(ExperimentalStdlibApi::class)
    override suspend fun deserialize(charset: Charset, typeInfo: TypeInfo, content: ByteReadChannel): Any? {
        content.awaitContent()
        val adapter = moshi.adapter<Any>(typeInfo.kotlinType!!)
        return adapter.fromJson(String(content.toByteArray()))
    }

    @OptIn(ExperimentalStdlibApi::class)
    override suspend fun serialize(
        contentType: ContentType,
        charset: Charset,
        typeInfo: TypeInfo,
        value: Any
    ): OutgoingContent? {
        val adapter = moshi.adapter<Any>(typeInfo.kotlinType!!)
        val json = adapter.toJson(value)
        return ByteArrayContent(json.toByteArray(), contentType)
    }
}

fun ContentNegotiation.Config.moshi(moshi: Moshi = Moshi.Builder().build()) {
    register(ContentType.Application.Json, MoshiConverter(moshi))
}

fun ContentNegotiation.Config.moshi(block: Moshi.Builder.() -> Unit) {
    moshi(Moshi.Builder().apply(block).build())
}