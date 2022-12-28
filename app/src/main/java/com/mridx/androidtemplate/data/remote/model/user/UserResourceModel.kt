package com.mridx.androidtemplate.data.remote.model.user

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize
import com.mridx.androidtemplate.data.remote.model.misc.CreatedResource


@Keep
@Parcelize
data class UserResourceModel(
    val type: String = "",
    val id: String = "",
    val attributes: UserAttributesModel = UserAttributesModel()
) : Parcelable


@Keep
@Parcelize
data class UserAttributesModel(
    val name: String = "",
    val email: String = "",
    val role: String = "",
    val created: CreatedResource? = null,
) : Parcelable
