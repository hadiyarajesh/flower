package com.hadiyarajesh.flowersample.data.network

import com.hadiyarajesh.flowersample.data.database.entity.Quote
import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonClass
import com.squareup.moshi.ToJson

class QuoteAdapter  {
    @ToJson
    fun toJson(_quote: Quote): QuoteJson {
        return _quote.run { QuoteJson(_internalId, AuthorWrapper(author),QuoteWrapper(quote),authorId) }
    }

    @FromJson
    fun fromJson(quoteJson: QuoteJson): Quote {
        return quoteJson.run {  Quote(id,1,_author,_quote,_authorId) }
    }
}


@JsonClass(generateAdapter = true)
data class AuthorWrapper(val rendered: String)
@JsonClass(generateAdapter = true)
data class QuoteWrapper(val rendered: String)

@JsonClass(generateAdapter = true)
data class QuoteJson(val id: Int,val title: AuthorWrapper,val content: QuoteWrapper,val author: Int) {
    val _author: String
    get() {
        return title.rendered
    }
    val _quote:String
    get() {
        return content.rendered
    }
    val _authorId: Int
    get() {
        return author
    }
}