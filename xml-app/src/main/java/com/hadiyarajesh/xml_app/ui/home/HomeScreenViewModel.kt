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

package com.hadiyarajesh.xml_app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hadiyarajesh.xml_app.database.entity.Image
import com.hadiyarajesh.xml_app.repository.ImageRepository
import com.hadiyarajesh.xml_app.util.UiState
import com.hadiyarajesh.flower_core.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val imageRepository: ImageRepository
) : ViewModel() {
    private val _images = MutableStateFlow<UiState<List<Image>>>(UiState.Empty)
    val images: StateFlow<UiState<List<Image>>> get() = _images

    private val _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        getAllImages()
    }

    private fun getAllImages(shouldMakeNetworkRequest: Boolean? = null) = viewModelScope.launch {
        val randomPageNumber = Random.nextInt(0, 20)

        imageRepository
            .getAllImages(
                page = randomPageNumber,
                shouldMakeNetworkRequest = shouldMakeNetworkRequest
            ).collect { response ->
                when (response.status) {
                    is Resource.Status.Loading -> {
                        _isLoading.value = true
                        _images.value = UiState.Loading
                    }

                    is Resource.Status.Success -> {
                        _isLoading.value = false
                        val images = (response.status as Resource.Status.Success).data
                        _images.value = UiState.Success(images.shuffled())
                    }

                    is Resource.Status.EmptySuccess -> {
                        _isLoading.value = false
                    }

                    is Resource.Status.Error -> {
                        _isLoading.value = false
                        val errorMessage = (response.status as Resource.Status.Error).errorMessage
                        _images.value = UiState.Error(errorMessage)
                    }
                }
            }
    }

    fun refreshImages() = getAllImages(shouldMakeNetworkRequest = true)
}
