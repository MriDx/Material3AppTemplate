package com.mridx.androidtemplate.data.local.model.listing

import androidx.annotation.Keep

@Keep
data class UIError(
    val showError: Boolean = false,
    val errorMessage: String? = null,
) {
    companion object {
        fun show(message: String): UIError {
            return UIError(showError = true, errorMessage = message)
        }

        fun hide(): UIError {
            return UIError(showError = false)
        }
    }
}