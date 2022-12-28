package com.mridx.androidtemplate.presentation.app.activity

import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.*
import androidx.transition.Fade
import androidx.transition.TransitionManager
import dagger.hilt.android.AndroidEntryPoint
import com.mridx.androidtemplate.R
import com.mridx.androidtemplate.databinding.AppActivityBinding
import com.mridx.androidtemplate.di.qualifier.AppPreference
import com.mridx.androidtemplate.presentation.base.activity.BaseActivity
import com.mridx.androidtemplate.utils.ROLE
import javax.inject.Inject

@AndroidEntryPoint
class AppActivity : BaseActivity() {

    private lateinit var binding: AppActivityBinding

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    private var from: String? = ""

    @AppPreference
    @Inject
    lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding =
            DataBindingUtil.setContentView<AppActivityBinding?>(this, R.layout.app_activity).apply {
                    setLifecycleOwner { lifecycle }
                }

        setSupportActionBar(binding.appBar.toolbar)


        setupNavigation()


        /*
        navController = findNavController(R.id.fragmentContainer)

        appBarConfiguration = AppBarConfiguration.Builder(
            topLevelDestinationIds = setOf(
                R.id.homeFragment,
                R.id.schemeListFragment,
                R.id.meetingsFragment,
                R.id.noticeHolderFragment,
            )
        ).build()


        binding.appBar.toolbar.setupWithNavController(
            navController = navController, configuration = appBarConfiguration
        )


        binding.bottomNavigationView.setupWithNavController(navController)


        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            TransitionManager.beginDelayedTransition(binding.bottomNavigationView, Fade())
            when (destination.id) {
                R.id.homeFragment, R.id.meetingsFragment, R.id.schemeListFragment, R.id.noticeHolderFragment -> {
                    binding.bottomNavigationView.isVisible = true
                }
                else -> {
                    binding.bottomNavigationView.isVisible = false
                }
            }
        }
        */


    }

    private fun setupNavigation() {

        navController = findNavController(R.id.fragmentContainer)

        val role = sharedPreferences.getString(ROLE, "admin") ?: "admin"

        if (role.contentEquals("contractor", true)) {

            appBarConfiguration = AppBarConfiguration(navController.graph)

            binding.appBar.toolbar.setupWithNavController(
                navController = navController, configuration = appBarConfiguration
            )

            //binding.bottomNavigationView.isVisible = false


            return

        }



        appBarConfiguration =
            AppBarConfiguration.Builder(topLevelDestinationIds = setOf(R.id.homeFragment)).build()


        binding.appBar.toolbar.setupWithNavController(
            navController = navController, configuration = appBarConfiguration
        )

        //binding.bottomNavigationView.setupWithNavController(navController)


        /*navController.addOnDestinationChangedListener { controller, destination, arguments ->
            TransitionManager.beginDelayedTransition(binding.bottomNavigationView, Fade())
            when (destination.id) {
                R.id.homeFragment, R.id.meetingsFragment, R.id.schemeListFragment, R.id.noticeHolderFragment -> {
                    binding.bottomNavigationView.isVisible = true
                }
                else -> {
                    binding.bottomNavigationView.isVisible = false
                }
            }
        }*/


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