package com.mridx.androidtemplate.data.remote.model.misc

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Keep
@Parcelize
data class PaginatedResponseModel<Data>(
    val data: @RawValue Data,
    val current_page: Int,
    val from: Int,
    val per_page: Int,
    val to: Int,
    val first_page_url: String? = null,
    val next_page_url: String? = null,
    val path: String? = null,
    val prev_page_url: String? = null,
) : Parcelable
