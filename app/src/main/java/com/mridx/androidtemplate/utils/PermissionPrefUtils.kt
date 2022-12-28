package com.mridx.androidtemplate.utils

import android.app.Activity
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import com.mridx.androidtemplate.utils.appSettings


const val LOCATION_PERMISSION = "location_permission"
const val CAMERA_PERMISSION = "camera_permission"
const val STORAGE_PERMISSION = "storage_permission"

fun checkIfAlreadyAskedPermission(
    sharedPreferences: SharedPreferences,
    permission: String
): Boolean {
    return sharedPreferences.getBoolean(permission, false)
}

fun setPermissionAskedPreference(sharedPreferences: SharedPreferences, permission: String) {
    sharedPreferences.edit {
        putBoolean(permission, true)
    }
}


fun openAppSettings(context: Activity, fragment: Fragment?) {
    if (fragment != null)
        fragment.appSettings()
    else
        context.appSettings()
}