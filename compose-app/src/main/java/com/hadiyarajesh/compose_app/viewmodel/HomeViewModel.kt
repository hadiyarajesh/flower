package com.hadiyarajesh.compose_app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.hadiyarajesh.compose_app.database.entity.Image
import com.hadiyarajesh.compose_app.repository.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeRepository: HomeRepository
) : ViewModel() {
    @ExperimentalPagingApi
    val images: Flow<PagingData<Image>> =
        homeRepository
            .getAllImages()
            .cachedIn(viewModelScope)
}
