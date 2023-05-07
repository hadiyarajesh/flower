package com.example.xml_app.fragment.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.xml_app.database.entity.Image
import com.example.xml_app.repository.ImageRepository
import com.example.xml_app.util.UiState
import com.hadiyarajesh.flower_core.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class ProfileListViewModel @Inject constructor(
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
