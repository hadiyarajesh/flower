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

package com.hadiyarajesh.flowersample.data.network

import com.hadiyarajesh.flowersample.data.database.entity.Quote
import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonClass
import com.squareup.moshi.ToJson

class QuoteAdapter  {
    @ToJson
    fun toJson(_quote: Quote): QuoteJson {
        return _quote.run { QuoteJson(_internalId, Title(title),Content(quote),author) }
    }

    @FromJson
    fun fromJson(quoteJson: QuoteJson): Quote{
        return quoteJson.run {  Quote(id,title.rendered,content.rendered,author,1) }
    }
}


@JsonClass(generateAdapter = true)
data class Title(val rendered: String)

@JsonClass(generateAdapter = true)
data class Content(val rendered: String)

@JsonClass(generateAdapter = true)
data class QuoteJson(val id: Int, val title: Title, val content: Content,val author: Int)
