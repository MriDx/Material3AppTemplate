package com.mridx.androidtemplate.data.remote.model.home

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class HomePageModel(
    val name: String = ""
) : Parcelable
