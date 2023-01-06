package com.mridx.androidtemplate.presentation.base.fragment.upload.event

import android.graphics.Typeface

sealed class MediaUploadFragmentEvent {

    object EnableGPS : MediaUploadFragmentEvent()

    object StartLocationUpdates : MediaUploadFragmentEvent()

    data class Upload(
        val filePath: String,
        val processedImagePath: String,
        val typeface: Typeface,
        val waterMarks: MutableMap<String, String> = mutableMapOf(),
    ) : MediaUploadFragmentEvent()

}
