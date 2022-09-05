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

package com.hadiyarajesh.compose_app.repository

import com.hadiyarajesh.compose_app.database.dao.ImageDao
import com.hadiyarajesh.compose_app.database.entity.Image
import com.hadiyarajesh.compose_app.network.ImageApi
import com.hadiyarajesh.flower_core.Resource
import com.hadiyarajesh.flower_core.dbBoundResource
import com.hadiyarajesh.flower_core.networkResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageRepository @Inject constructor(
    private val imageApi: ImageApi,
    private val imageDao: ImageDao
) {
    fun getAllImages(
        page: Int,
        size: Int = 50,
        shouldMakeNetworkRequest: Boolean? = null
    ): Flow<Resource<List<Image>>> {
        return dbBoundResource(
            fetchFromLocal = { imageDao.getAllImages() },
            shouldMakeNetworkRequest = { dbData ->
                shouldMakeNetworkRequest ?: dbData.isNullOrEmpty()
            },
            makeNetworkRequest = {
                imageApi.getAllImages(
                    page = page,
                    size = size
                )
            },
            processNetworkResponse = {},
            saveResponseData = { images ->
                imageDao.deleteAllImages()
                imageDao.insertAllImages(images)
            },
            onNetworkRequestFailed = { _, _ -> }
        ).flowOn(Dispatchers.IO)
    }

    fun getImage(imageId: Long): Flow<Resource<Image>> {
        return networkResource(
            makeNetworkRequest = { imageApi.getImage(imageId = imageId) },
            onNetworkRequestFailed = { _, _ -> }
        ).flowOn(Dispatchers.IO)
    }
}
