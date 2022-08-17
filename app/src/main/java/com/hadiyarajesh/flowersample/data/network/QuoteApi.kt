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

import com.hadiyarajesh.flower_core.ApiResponse
import com.hadiyarajesh.flowersample.data.database.entity.Quote
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET
import retrofit2.http.Query

interface QuoteApi {
    @GET("posts/?orderby=id&per_page=1")
    fun getRandomQuote(@Query("page") pageNo: Int): Flow<ApiResponse<List<Quote>>>
}
