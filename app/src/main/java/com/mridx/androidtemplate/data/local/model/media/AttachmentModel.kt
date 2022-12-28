package com.mridx.androidtemplate.data.local.model.media

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize


enum class AttachmentType {
    DOCUMENT,
    IMAGE
}

fun getAttachmentType(type: AttachmentType) : String {
    return when (type) {
        AttachmentType.IMAGE -> "Image"
        AttachmentType.DOCUMENT -> "Document"
    }
}

fun getDocumentType(name: String): AttachmentType {
    return when (name.split(".").last().lowercase()) {
        "jpg", "png", "jpeg" -> AttachmentType.IMAGE
        else -> AttachmentType.DOCUMENT
    }
}


@Keep
@Parcelize
data class AttachmentModel(
    val type: AttachmentType,
    val name: String,
    val url: String
) : Parcelable
