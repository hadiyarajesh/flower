/*
 *  Copyright (C) 2023 Rajesh Hadiya
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

package com.hadiyarajesh.xml_app.repository

import com.hadiyarajesh.flower_core.Resource
import com.hadiyarajesh.xml_app.database.entity.Image
import kotlinx.coroutines.flow.Flow

interface ImageRepository {
    fun getAllImages(
        page: Int,
        size: Int = 50,
        shouldMakeNetworkRequest: Boolean? = null
    ): Flow<Resource<List<Image>>>


    fun getImageFromDb(imageId: Long): Flow<Resource<Image>>

    fun getImageFromNetwork(imageId: Long): Flow<Resource<Image>>
}
