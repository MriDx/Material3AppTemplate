package com.mridx.androidtemplate.presentation.auth.fragment.logout.vm

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.mridx.androidtemplate.domain.use_case.user.LogoutUseCase
import com.mridx.androidtemplate.presentation.auth.fragment.logout.event.LogoutDialogEvent
import com.mridx.androidtemplate.presentation.auth.fragment.logout.state.LogoutDialogState
import com.mridx.androidtemplate.presentation.base.vm.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class LogoutDialogViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase,
) : BaseViewModel<LogoutDialogEvent, LogoutDialogState>() {


    override fun handleEvent(event: LogoutDialogEvent) {
        when (event) {
            is LogoutDialogEvent.Logout -> {
                logoutUser()
            }
        }
    }


    private fun logoutUser() {
        viewModelScope.launch(Dispatchers.IO) {

            val response = logoutUseCase()

            val state = if (response.isFailed()) {
                LogoutDialogState.LogoutFailed(
                    response = response.data,
                    message = response.message
                )
            } else {
                LogoutDialogState.LogoutSuccess
            }

            sendState(state)

        }
    }

}