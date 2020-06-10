package com.hadiyarajesh.flowersample.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.hadiyarajesh.flower.Resource
import com.hadiyarajesh.flowersample.data.database.entity.Quote
import com.hadiyarajesh.flowersample.data.repository.QuoteRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.map

class MainActivityViewModel(
    quoteRepository: QuoteRepository
) : ViewModel() {

    @ExperimentalCoroutinesApi
    val randomQuote: LiveData<Resource<Quote>> = quoteRepository.getRandomQuote().map {
        when (it.status) {
            Resource.Status.LOADING -> {
                Resource.loading(null)
            }
            Resource.Status.SUCCESS -> {
                val quote = it.data
                Resource.success(quote)
            }
            Resource.Status.ERROR -> {
                Resource.error(it.message!!, null)
            }
        }
    }.asLiveData(viewModelScope.coroutineContext)
}

