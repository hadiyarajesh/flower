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
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.hadiyarajesh.compose_app.database.FlowerDatabase
import com.hadiyarajesh.compose_app.database.dao.ImageDao
import com.hadiyarajesh.compose_app.database.entity.Image
import com.hadiyarajesh.compose_app.network.ImageApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeRepository @Inject constructor(
    private val imageApi: ImageApi,
    private val imageDao: ImageDao,
    private val db: FlowerDatabase
) {
    @ExperimentalPagingApi
    fun getAllImages(): Flow<PagingData<Image>> = Pager(
        config = PagingConfig(pageSize = 9),
        remoteMediator = ImageRemoteMediator(db = db, imageApi = imageApi)
    ) {
        db.imageDao.getAllImages()
    }.flow
}
