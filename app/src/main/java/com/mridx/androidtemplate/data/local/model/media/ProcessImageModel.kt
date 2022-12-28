package com.mridx.androidtemplate.data.local.model.media

import android.graphics.Typeface
import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
data class ProcessImageModel(
    val filePath: String,
    val processedImagePath: String,
    val typeface: Typeface,
    val waterMarks: MutableMap<String, String> = mutableMapOf(),
)
