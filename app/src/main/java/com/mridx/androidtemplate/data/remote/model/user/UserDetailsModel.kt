package com.mridx.androidtemplate.data.remote.model.user

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class UserDetailsModel(
    val id: Int,
    val uuid: String,
    val name: String,
    val email: String,
    val phone: String? = null,
    val designation: String? = null,
    val working_division: String? = null,
    val gender: String? = null,
    val address: String? = null,
    val date_of_joining: String? = null,
    val bio: String? = null,
    val profile_pic: String? = null,
    val role_name: String? = null,
    val user_lac: String? = null,
    val user_division: String? = null,
    val profile_url: String? = null,
    val newdivision_name: String? = null,
) : Parcelable
