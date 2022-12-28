package com.mridx.androidtemplate.data.local.model.action

import androidx.annotation.DrawableRes
import androidx.annotation.Keep

@Keep
data class ActionModel(
    val heading: String,
    @DrawableRes val icon: Int,
    val id: String
)
