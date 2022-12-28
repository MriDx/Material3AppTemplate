package com.mridx.androidtemplate.data.remote.model.user

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class LoginResponseModel(
    val token_type: String? = null,
    val access_token: String? = null,
    val user: UserResourceModel? = null
) : Parcelable {

    fun getAccessToken() = "$token_type $access_token"

}