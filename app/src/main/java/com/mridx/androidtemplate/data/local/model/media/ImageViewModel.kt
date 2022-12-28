package com.mridx.androidtemplate.data.local.model.media

import android.os.Parcelable
import androidx.annotation.Keep
import com.mridx.androidtemplate.data.remote.model.misc.ImageModel
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class ImageViewModel(
    val imageModel: ImageModel?,
    val caption: String? = null
) : Parcelable