package com.mridx.androidtemplate.data.remote.model.misc

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.mridx.androidtemplate.utils.isNull
import kotlinx.parcelize.Parcelize
import java.lang.reflect.Type

@Keep
@Parcelize
data class ImageModel(
    val small: String? = null,
    val original: String? = null,
) : Parcelable


class ImageModelParserAdapter : JsonDeserializer<ImageModel?> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): ImageModel? {
        if (json.isNull()) return null
        if (json!!.isJsonArray) return null
        return with(json.asJsonObject) {
            val small = get("small").asString
            val original = get("original").asString
            ImageModel(small = small, original = original)
        }
    }
}
