package com.example.xml_app.repository

import com.example.xml_app.database.dao.ImageDao
import com.example.xml_app.database.entity.Image
import com.example.xml_app.network.ImageApi
import com.hadiyarajesh.flower_core.Resource
import com.hadiyarajesh.flower_core.dbBoundResource
import com.hadiyarajesh.flower_core.dbResource
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

    fun getImageFromDb(imageId: Long): Flow<Resource<Image>> {
        return dbResource { imageDao.getImageById(imageId) }.flowOn(Dispatchers.IO)
    }

    fun getImageFromNetwork(imageId: Long): Flow<Resource<Image>> {
        return networkResource(
            makeNetworkRequest = { imageApi.getImage(imageId = imageId) },
            onNetworkRequestFailed = { _, _ -> }
        ).flowOn(Dispatchers.IO)
    }
}