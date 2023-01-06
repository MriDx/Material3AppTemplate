package com.mridx.androidtemplate.data.local.constants.settings_fragment

import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.annotation.Keep
import androidx.annotation.StringRes
import com.mridx.androidtemplate.BuildConfig
import kotlinx.parcelize.Parcelize
import com.mridx.androidtemplate.R


@Keep
@Parcelize
data class SettingsItemModel(
    @StringRes val heading: Int,
    @DrawableRes val icon: Int,
    val actionId: String,
    var subHeading: String? = null,
    @StringRes var subHeadingRes: Int = 0,
) : Parcelable {

    companion object {
        fun isBlank(): SettingsItemModel {
            return SettingsItemModel(0, 0, "")
        }
    }

    fun isBlank(): Boolean {
        return actionId.isEmpty()
    }

    fun hasSubHeading(): Boolean {
        return !subHeading.isNullOrEmpty() || subHeadingRes != 0
    }


}


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
    SettingsItemModel.isBlank(),
    SettingsItemModel(
        heading = R.string.settingsFragmentPrivacyPolicy,
        icon = R.drawable.ic_outline_privacy_tip_24,
        actionId = "privacy_policy"
    ),
    SettingsItemModel(
        heading = R.string.settingsFragmentAppVersion,
        icon = R.drawable.ic_outline_info_24,
        actionId = "app_info",
        subHeading = BuildConfig.VERSION_NAME
    ),
    SettingsItemModel(
        heading = R.string.settingsFragmentPoweredBy,
        icon = R.drawable.ic_baseline_android_24,
        actionId = "powered_by",
        subHeadingRes = R.string.app_powered_by
    ),
)