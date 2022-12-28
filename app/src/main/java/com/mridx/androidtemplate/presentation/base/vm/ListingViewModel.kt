package com.mridx.androidtemplate.presentation.base.vm

import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mridx.androidtemplate.data.local.model.listing.UIError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

abstract class ListingViewModel<Event, State> : BaseViewModel<Event, State>() {


    /*var uiError: ObservableField<UIError> = ObservableField(UIError.hide())

    open fun setUIError(uiError: UIError) {
        this.uiError.set(uiError)
        this.uiError.notifyChange()
    }*/


    private var errorState_ = Channel<UIError>()
    val errorState = errorState_.receiveAsFlow()

    open fun setErrorState(uiError: UIError) {
        viewModelScope.launch(Dispatchers.IO) {
            errorState_.send(uiError)
        }
    }


}