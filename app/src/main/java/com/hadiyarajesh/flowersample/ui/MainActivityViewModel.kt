package com.hadiyarajesh.flowersample.ui

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.hadiyarajesh.flower.Resource
import com.hadiyarajesh.flowersample.data.database.entity.Quote
import com.hadiyarajesh.flowersample.data.repository.QuoteRepository
import com.hadiyarajesh.flowersample.extensions.foldApiStates
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    quoteRepository: QuoteRepository
) : ViewModel() {
    val currentPageNo: MutableLiveData<Int> = MutableLiveData(1)

    private val commandsChannel = Channel<Command>()
    private val commands = commandsChannel.receiveAsFlow()

    private val oneShotEventsChannel = Channel<Event>()
    val events = oneShotEventsChannel.receiveAsFlow()

    init {
        changePage(1)
    }

    private val getQuotesForPage = { page: Int ->
        quoteRepository.getRandomQuote(page, onFailed = { errorBody, statusCode ->
            Log.i("getRandomQuote", "onFailure => $errorBody ,$statusCode")
            viewModelScope.launch {
                errorBody?.let { oneShotEventsChannel.send(Event.Error(it)) }
            }
            currentPageNo.postValue(currentPageNo.value?.minus(1))
        })
    }

    val quotes = commands.flatMapLatest { command ->
        flow {
            when (command) {
                is Command.ChangePageCommand -> {
                    getQuotesForPage(command.page).foldApiStates({ quote ->
                        delay(250)
                        emit(State.UIState(quote, currentPageNo.value ?: 1))
                    }, { emit(it) }, { emit(it) })
                }
            }

        }
    }.asLiveData(viewModelScope.coroutineContext)

    fun changePage(page: Int) {
        viewModelScope.launch {
            currentPageNo.value = page
            commandsChannel.send(Command.ChangePageCommand(page))
        }
    }

    sealed class Command {
        data class ChangePageCommand(val page: Int) : Command()
    }

    sealed class State {
        data class UIState(val quote: Quote, val currentPage: Int) : State()
        data class SuccessState(val resource: Resource<*>) : State()
        data class ErrorState(val errorMessage: String) : State()
        data class LoadingState(val loading: Boolean = true) : State()
    }

    sealed class Event {
        data class Error(val message: String) : Event()
    }
}
