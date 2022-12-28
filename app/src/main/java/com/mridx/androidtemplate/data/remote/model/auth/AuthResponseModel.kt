package com.mridx.androidtemplate.data.remote.model.auth

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class AuthResponseModel(
    val access_token: String
) : Parcelable {

    fun getAccessToken(): String = "Bearer $access_token"

}
