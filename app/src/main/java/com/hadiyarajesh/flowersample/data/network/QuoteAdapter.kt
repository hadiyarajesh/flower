package com.hadiyarajesh.flowersample.data.network

import com.hadiyarajesh.flowersample.data.database.entity.Quote
import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonClass
import com.squareup.moshi.ToJson


class QuoteAdapter {
    @ToJson
    fun toJson(_quote: Quote): QuoteJson {
        return _quote.run { QuoteJson(_internalId, Title(title), Content(quote), author) }
    }

    @FromJson
    fun fromJson(quoteJson: QuoteJson): Quote {
        return quoteJson.run { Quote(id, title.rendered, content.rendered, author, 1) }
    }
}


@JsonClass(generateAdapter = true)
data class Title(val rendered: String)

@JsonClass(generateAdapter = true)
data class Content(val rendered: String)

@JsonClass(generateAdapter = true)
data class QuoteJson(val id: Int, val title: Title, val content: Content, val author: Int)