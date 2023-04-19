package com.mridx.androidtemplate.presentation.base.fragment.permission_launcher

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.google.android.gms.common.api.ResolvableApiException
import com.mridx.androidtemplate.BuildConfig
import com.mridx.androidtemplate.domain.use_case.location.GpsResult
import com.mridx.androidtemplate.presentation.base.fragment.base.BaseFragment

abstract class PermissionLauncherFragment<ViewBinding> : BaseFragment<ViewBinding>() {

    /**
     * Launches Runtime Permissions Request dialog
     * can request multiple permissions at a time
     *
     * usage
     * requestPermissionsLauncher.launch(arrayOf(permissions))
     *
     * handle permissions results
     * override permissionsResults function
     *
     */
    private val requestPermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { grantedPermissions ->
            permissionsResults(permissions = grantedPermissions)
        }

    open fun permissionsResults(permissions: Map<String, Boolean>) {

    }

    open fun requestPermissions(permissions: Array<String>) {
        requestPermissionsLauncher.launch(permissions)
    }


    /**
     *
     * Launches App settings activity to allow manual permission
     *
     * usage
     * appSettingsLauncher.launch(Intent().apply {
    action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
    data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
    })
     *
     * override onAppSettingsActivityResult to grab the result
     */
    private val appSettingsLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            onAppSettingsActivityResult(it)
        }

    open fun onAppSettingsActivityResult(activityResult: ActivityResult) {

    }

    open fun launchAppSettings() {
        appSettingsLauncher.launch(Intent().apply {
            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
        })
    }


    private val gpsExceptionResolverLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
            onGpsExceptionActivityResult(it)
        }

    open fun onGpsExceptionActivityResult(activityResult: ActivityResult) {

    }

    open fun handleGPSException(gpsResult: GpsResult): Boolean {
        var handled = false
        (gpsResult.exception as? ResolvableApiException)?.resolution?.intentSender?.let {
            handled = true
            gpsExceptionResolverLauncher.launch(IntentSenderRequest.Builder(it).build())
        }
        return handled
    }


}