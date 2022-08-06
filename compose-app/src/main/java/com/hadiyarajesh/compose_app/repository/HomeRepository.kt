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
