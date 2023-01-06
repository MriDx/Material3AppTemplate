package com.mridx.androidtemplate.presentation.user.fragment.password.state

import com.google.gson.JsonElement
import com.mridx.androidtemplate.data.remote.model.misc.ResponseModel
import com.mridx.androidtemplate.data.remote.model.utils.Resource

sealed class ChangePasswordFragmentState {

    data class Response(
        val response: Resource<ResponseModel<JsonElement>>? = null,
        val message: String?
    ): ChangePasswordFragmentState()

}
