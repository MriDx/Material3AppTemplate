package com.mridx.androidtemplate.data.local.model.user

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize
import com.mridx.androidtemplate.utils.*

@Keep
@Parcelize
data class UserModel(
    val name: String = "",
    val email: String = "",
    val designation: String = "",
    val phone: String = "",
    val photo: String = "",
    val role: String = "",
    val joined: String = "",
) : Parcelable {

    companion object {

        fun fromMap(details: Map<String, *>): UserModel {
            return UserModel(
                name = details[USER_NAME].toString(),
                email = details[USER_EMAIL].toString(),
                designation = details[DESIGNATION].toString(),
                phone = details[PHONE].toString(),
                photo = details[PHOTO].toString(),
                role = details[ROLE].toString(),
                joined = details[JOINED].toString(),
            )
        }

    }


    fun getMapForProfile(): Map<String, String?> {
        return mapOf(
            "Email" to email,
            "Phone" to phone,
            "Joined on" to joined
        )
    }


}
