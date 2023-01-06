package com.sumato.ino.officer.presentation.user.password.event

import org.json.JSONObject

sealed class ChangePasswordFragmentEvent {

    data class Change(
        val currentPassword: String,
        val newPassword: String,
        val newConfirmationPassword: String
    ) : ChangePasswordFragmentEvent() {

        fun toJson(): JSONObject {
            return JSONObject().apply {
                put("current_password", currentPassword)
                put("password", newPassword)
                put("password_confirmation", newConfirmationPassword)
            }
        }

    }

}