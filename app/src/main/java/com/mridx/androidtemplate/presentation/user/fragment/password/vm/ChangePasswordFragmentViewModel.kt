package com.mridx.androidtemplate.presentation.user.fragment.password.vm

import androidx.lifecycle.viewModelScope
import com.mridx.androidtemplate.domain.use_case.user.ChangePasswordUseCase
import com.mridx.androidtemplate.presentation.base.vm.BaseViewModel
import com.sumato.ino.officer.presentation.user.password.event.ChangePasswordFragmentEvent
import com.mridx.androidtemplate.presentation.user.fragment.password.state.ChangePasswordFragmentState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChangePasswordFragmentViewModel @Inject constructor(
    private val changePasswordUseCase: ChangePasswordUseCase,
) :
    BaseViewModel<ChangePasswordFragmentEvent, ChangePasswordFragmentState>() {



    override fun handleEvent(event: ChangePasswordFragmentEvent) {
        when (event) {
            is ChangePasswordFragmentEvent.Change -> {
                changePassword(event)
            }
        }
    }

    private fun changePassword(event: ChangePasswordFragmentEvent.Change) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = changePasswordUseCase.execute(event.toJson())

            val state = ChangePasswordFragmentState.Response(
                response = response,
                message = response.message
            )

            sendState(state)

        }
    }
}