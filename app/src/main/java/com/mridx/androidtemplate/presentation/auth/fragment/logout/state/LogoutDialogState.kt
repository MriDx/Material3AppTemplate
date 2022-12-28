package com.mridx.androidtemplate.presentation.auth.fragment.logout.state

import com.google.gson.JsonElement
import com.mridx.androidtemplate.data.remote.model.misc.ResponseModel

sealed class LogoutDialogState {

    object LogoutSuccess : LogoutDialogState()

    data class LogoutFailed(
        val response: ResponseModel<JsonElement>? = null,
        val message: String? = null
    ) : LogoutDialogState()

}