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

package com.hadiyarajesh.xml_app.fragment.detail

import androidx.lifecycle.ViewModel
import com.hadiyarajesh.xml_app.database.entity.Image
import com.hadiyarajesh.xml_app.repository.ImageRepository
import com.hadiyarajesh.xml_app.util.LoadResourceFrom
import com.hadiyarajesh.xml_app.util.UiState
import com.hadiyarajesh.flower_core.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ProfileDetailViewModel @Inject constructor(
    private val imageRepository: ImageRepository
) : ViewModel() {
    private val _image = MutableStateFlow<UiState<Image>>(UiState.Empty)
    val image: StateFlow<UiState<Image>> get() = _image

    private val _loadFrom = MutableStateFlow<LoadResourceFrom>(LoadResourceFrom.Db)
    val loadFrom: StateFlow<LoadResourceFrom> get() = _loadFrom

    private fun getLoadFunction(imageId: Long, loadFrom: LoadResourceFrom) =
        if (loadFrom == LoadResourceFrom.Db) imageRepository.getImageFromDb(imageId)
        else imageRepository.getImageFromNetwork(imageId = imageId)

    suspend fun getImage(imageId: Long, loadFrom: LoadResourceFrom) =
        getLoadFunction(imageId, loadFrom).collect { response ->
            _loadFrom.value = loadFrom

            when (response.status) {
                is Resource.Status.Loading -> {
                    _image.value = UiState.Loading
                }

                is Resource.Status.Success -> {
                    val image = (response.status as Resource.Status.Success).data
                    _image.value = UiState.Success(image)
                }

                is Resource.Status.EmptySuccess -> {}

                is Resource.Status.Error -> {
                    val errorMessage = (response.status as Resource.Status.Error).errorMessage
                    UiState.Error(errorMessage)
                }
            }
        }
}
