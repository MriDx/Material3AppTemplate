package com.mridx.androidtemplate.presentation.splash.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.mridx.androidtemplate.BuildConfig
import com.mridx.androidtemplate.R
import com.mridx.androidtemplate.databinding.SplashActivityBinding
import com.mridx.androidtemplate.di.qualifier.AppPreference
import com.mridx.androidtemplate.presentation.app.activity.AppActivity
import com.mridx.androidtemplate.presentation.auth.activity.AuthActivity
import com.mridx.androidtemplate.presentation.base.activity.BaseActivity
import com.mridx.androidtemplate.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : BaseActivity() {

    private lateinit var binding: SplashActivityBinding

    @Inject
    @AppPreference
    lateinit var sharedPreferences: SharedPreferences

    private val appUpdateManager by lazy { AppUpdateManagerFactory.create(this) }

    private var IMMEDIATE_UPDATE_CODE = 1001

    private var message = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding =
            DataBindingUtil.setContentView<SplashActivityBinding?>(this, R.layout.splash_activity)
                .apply {
                    setLifecycleOwner { lifecycle }
                }


        /**
         * will receive message in case of auto logged out
         */
        message = intent?.getStringExtra("message") ?: ""

        val bundle = intent?.extras

        /**
         * checking if the bundle data are from notification
         * if yes, starting notification activity
         *
         * Note - Replace body with action when needed
         */
        if (bundle?.containsKey("body") == true) {
            val intent = Intent(this, AppActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK + Intent.FLAG_ACTIVITY_CLEAR_TASK
            intent.putExtras(
                bundleOf(
                    "from" to "notification"
                )
            )
            startActivity(intent)
            finish()
            return
        }


        checkAppUpdate()


    }


    private fun handleSplashWork() {
//        startActivity(MainActivity::class.java)
//        return
        lifecycleScope.launch(Dispatchers.Default) {
            val logged = sharedPreferences.getBoolean(LOGGED_IN, false)
            withContext(Dispatchers.Main) {
                if (logged) {
                    startActivity(AppActivity::class.java)
                } else
                    startActivity(AuthActivity::class.java) {
                        bundleOf(
                            "message" to message
                        )
                    }
                finish()
            }
        }
    }


    private fun continueSplashWork() {
        lifecycleScope.launch {
            delay(SPLASH_DURATION)
            handleSplashWork()
        }
    }

    private fun checkAppUpdate() {
        lifecycleScope.launch(Dispatchers.IO) {
            val updateInfoTask = appUpdateManager.appUpdateInfo
            updateInfoTask
                .addOnSuccessListener { info ->
                    if (info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                        || info.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
                    ) {
                        invokeUpdate(
                            type = AppUpdateType.IMMEDIATE,
                            appUpdateInfo = info,
                            code = IMMEDIATE_UPDATE_CODE
                        )
                        return@addOnSuccessListener
                    }
                    continueSplashWork()
                }
                .addOnFailureListener {
                    continueSplashWork()
                }
        }
    }

    private fun invokeUpdate(type: Int, appUpdateInfo: AppUpdateInfo, code: Int) {
        appUpdateManager.startUpdateFlowForResult(
            appUpdateInfo,
            type,
            this,
            code
        )
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMMEDIATE_UPDATE_CODE) {
            if (requestCode != Activity.RESULT_OK) {
                //show failed dialog
                showDialog(
                    title = "Update Failed !",
                    message = "App auto update failed ! \nTry updating the app from play store to get the new changes and features.",
                    positiveBtn = "Open Play Store",
                    negativeBtn = "Later",
                    cancellable = false
                ) { d, i ->
                    if (i == POSITIVE_BTN) {
                        //open play store
                        openPlayStore()
                        return@showDialog
                    }
                    d.dismiss()
                    //continue
                }.show()
            }
        }
    }

    private fun openPlayStore() {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("market://details?id=" + BuildConfig.APPLICATION_ID)
        startActivity(intent)
        finish()
    }

}