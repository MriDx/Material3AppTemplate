package com.mridx.androidtemplate.presentation.auth.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import com.mridx.androidtemplate.R
import com.mridx.androidtemplate.databinding.AuthActivityBinding
import com.mridx.androidtemplate.presentation.base.activity.BaseActivity
import com.mridx.androidtemplate.utils.errorSnackbar
import com.mridx.androidtemplate.utils.infoSnackbar

@AndroidEntryPoint
class AuthActivity : BaseActivity() {


    private lateinit var binding: AuthActivityBinding

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    private var message: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView<AuthActivityBinding?>(this, R.layout.auth_activity)
            .apply {
                setLifecycleOwner { lifecycle }
            }


        message = intent?.getStringExtra("message")

        if (message != null && message!!.isNotEmpty()) {
            infoSnackbar(
                view = binding.root,
                message = message!!,
                duration = Snackbar.LENGTH_INDEFINITE,
                action = "OK"
            )
        }


        navController = findNavController(R.id.fragmentContainer)

        appBarConfiguration = AppBarConfiguration.Builder().build()


    }


    override fun onSupportNavigateUp(): Boolean {
        //return super.onSupportNavigateUp()
        if (!navController.navigateUp()) finish()
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

}