package com.mridx.androidtemplate.presentation.auth.fragment.login.vm

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.mridx.androidtemplate.domain.use_case.user.LoginUseCase
import com.mridx.androidtemplate.presentation.auth.fragment.login.event.LoginFragmentEvent
import com.mridx.androidtemplate.presentation.auth.fragment.login.state.LoginFragmentState
import com.mridx.androidtemplate.presentation.base.vm.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class LoginFragmentViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
) : BaseViewModel<LoginFragmentEvent, LoginFragmentState>() {


    override fun handleEvent(event: LoginFragmentEvent) {
        when (event) {
            is LoginFragmentEvent.LoginUser -> {
                //
                executeLogin(event = event)
            }
        }
    }


    private fun executeLogin(event: LoginFragmentEvent.LoginUser) {
        viewModelScope.launch(Dispatchers.IO) {

            val response = loginUseCase(params = event.toJson())

            val state = if (response.isFailed()) {
                LoginFragmentState.LoginFailed(
                    responseModel = response.data,
                    message = response.message
                )
            } else {
                LoginFragmentState.LoginSuccess
            }

            sendState(state = state)

        }
    }
}