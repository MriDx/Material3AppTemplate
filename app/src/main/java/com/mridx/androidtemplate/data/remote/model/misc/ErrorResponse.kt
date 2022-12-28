package com.mridx.androidtemplate.data.remote.model.misc

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.JsonElement
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Keep
@Parcelize
data class ErrorResponse(
    val status: Int,
    val message: String? = "Something went wrong ! Please try again after sometime.",
    val errors: @RawValue JsonElement? = null
) : Parcelable
