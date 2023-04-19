package com.mridx.androidtemplate.presentation.base.fragment.upload.event

import android.graphics.Typeface

sealed class MediaUploadFragmentEvent {

    data class Upload(
        val filePath: String,
        val processedImagePath: String,
        val typeface: Typeface,
        val waterMarks: MutableMap<String, String> = mutableMapOf(),
    ) : MediaUploadFragmentEvent()

}
