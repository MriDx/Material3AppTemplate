package com.mridx.androidtemplate.presentation.base.vm

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.withContext

abstract class BaseViewModel<Event, State> : ViewModel() {


    private var state_ = Channel<State>()
    val state = state_.receiveAsFlow()

    abstract fun addEvent(event: Event)

    open suspend fun sendState(state: State) {
        withContext(Dispatchers.IO) {
            state_.send(state)
        }
    }


}