package com.mridx.androidtemplate.presentation.auth.fragment.login.state

import com.mridx.androidtemplate.data.remote.model.auth.AuthResponseModel
import com.mridx.androidtemplate.data.remote.model.misc.ResponseModel
import com.mridx.androidtemplate.data.remote.model.user.LoginResponseModel

sealed class LoginFragmentState {

    object LoginSuccess : LoginFragmentState()

    data class LoginFailed(
        val responseModel: ResponseModel<AuthResponseModel>? = null,
        val message: String? = null
    ) : LoginFragmentState()

}
