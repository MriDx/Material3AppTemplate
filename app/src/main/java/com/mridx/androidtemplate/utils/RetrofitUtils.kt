package com.mridx.androidtemplate.utils

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject


fun JSONObject.toRequestBody(): RequestBody {
    return this.toString().toRequestBody("application/json".toMediaType())
}

fun String.toMultipartRequestBody(): RequestBody {
    return this.toRequestBody("multipart/form-data".toMediaType())
}