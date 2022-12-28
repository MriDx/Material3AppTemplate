package com.mridx.androidtemplate.data.remote.model.misc

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class CreatedResource(
    val human: String? = null,
    val date: String? = null,
    val formatted: String? = null
) : Parcelable
