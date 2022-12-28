package com.mridx.androidtemplate.data.remote.model.misc

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class FileUploadModel(@SerializedName("url") var url: String)