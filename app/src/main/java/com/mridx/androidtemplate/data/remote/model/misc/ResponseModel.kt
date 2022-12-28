package com.mridx.androidtemplate.data.remote.model.misc

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Keep
@Parcelize
data class ResponseModel<T>(
    val status: Int = 0,
    val message: String? = null,
    val data: @RawValue T? = null,
    val errorsResponse: ErrorResponse? = null
) : Parcelable {

    companion object {

        fun <T> withError(errorResponse: ErrorResponse?): ResponseModel<T> {
            return ResponseModel(errorsResponse = errorResponse)
        }

    }
}


@Keep
@Parcelize
data class StringResponseModel<T>(
    val status: String = "",
    val message: String? = null,
    val data: @RawValue T? = null,
    val errorsResponse: ErrorResponse? = null
) : Parcelable {

    companion object {

        fun <T> withError(errorResponse: ErrorResponse?): StringResponseModel<T> {
            return StringResponseModel(errorsResponse = errorResponse)
        }

    }
}