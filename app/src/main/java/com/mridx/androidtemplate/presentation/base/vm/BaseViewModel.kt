package com.mridx.androidtemplate.presentation.base.vm

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.withContext

abstract class BaseViewModel<Event, State> : ViewModel() {


    private var viewState_ = Channel<State>()
    val viewState = viewState_.receiveAsFlow()

    abstract fun handleEvent(event: Event)

    open suspend fun sendState(state: State) {
        withContext(Dispatchers.IO) {
            viewState_.send(state)
        }
    }


}