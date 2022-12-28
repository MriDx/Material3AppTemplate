package com.mridx.androidtemplate.data.local.constants.settings_fragment

import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.annotation.Keep
import androidx.annotation.StringRes
import kotlinx.parcelize.Parcelize
import com.mridx.androidtemplate.R


@Keep
@Parcelize
data class SettingsItemModel(
    @StringRes val heading: Int,
    @DrawableRes val icon: Int,
    val actionId: String
) : Parcelable


fun getSettingsItemsList(): List<SettingsItemModel> = listOf(
    SettingsItemModel(
        heading = R.string.settingsFragmentProfile,
        icon = R.drawable.ic_outline_account_circle_24,
        actionId = "profile"
    ),
    SettingsItemModel(
        heading = R.string.settingsFragmentLogout,
        icon = R.drawable.ic_baseline_power_settings_new_24,
        actionId = "logout"
    ),
)