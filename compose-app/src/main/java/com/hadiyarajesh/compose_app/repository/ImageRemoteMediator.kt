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

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.hadiyarajesh.compose_app.database.FlowerDatabase
import com.hadiyarajesh.compose_app.database.entity.Image
import com.hadiyarajesh.compose_app.database.entity.ImageRemoteKey
import com.hadiyarajesh.compose_app.network.ImageApi

@ExperimentalPagingApi
class ImageRemoteMediator(
    private val initialPage: Int = 0,
    private val db: FlowerDatabase,
    private val imageApi: ImageApi
) : RemoteMediator<Int, Image>() {
    private val imageDao = db.imageDao
    private val imageRemoteKeyDao = db.imageRemoteKeyDao

    override suspend fun initialize(): InitializeAction {
        // Require that remote REFRESH is launched on initial load and succeeds before launching
        // remote PREPEND / APPEND.
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Image>
    ): MediatorResult {
        try {
            val page = when (loadType) {
                LoadType.REFRESH -> initialPage
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val remoteKeys = db.withTransaction {
                        imageRemoteKeyDao.getAllKeysByDesc().lastOrNull()
                    } ?: throw NoSuchElementException("Last item not found in database")

                    remoteKeys.nextKey
                        ?: return MediatorResult.Success(endOfPaginationReached = true)
                }
            }

            val responseData = imageApi.getAllImages(page = page)

            val images = responseData
//            val endOfPaginationReached = !responseData.hasNextPage
            val endOfPaginationReached = true

            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    imageDao.deleteAllImages()
                    imageRemoteKeyDao.deleteAllKeys()
                }

                val prevKey = if (page == initialPage) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1

                val keys = images.map {
                    ImageRemoteKey(
                        imageId = it.id.toLong(),
                        prevKey = prevKey,
                        nextKey = nextKey
                    )
                }

                imageRemoteKeyDao.insertAllKeys(keys)
                imageDao.insertAllImages(images)
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: Throwable) {
            return MediatorResult.Error(e)
        }
    }
}
