package com.mridx.androidtemplate.presentation.auth.fragment.logout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.mridx.androidtemplate.R
import com.mridx.androidtemplate.databinding.LogoutDialogBinding
import com.mridx.androidtemplate.presentation.auth.fragment.logout.event.LogoutDialogEvent
import com.mridx.androidtemplate.presentation.auth.fragment.logout.state.LogoutDialogState
import com.mridx.androidtemplate.presentation.auth.fragment.logout.vm.LogoutDialogViewModel
import com.mridx.androidtemplate.presentation.base.fragment.rounded_bottomsheet_dialog.RoundedBottomSheetDialog
import com.mridx.androidtemplate.presentation.splash.activity.SplashActivity
import com.mridx.androidtemplate.utils.errorSnackbar
import com.mridx.androidtemplate.utils.startActivity

@AndroidEntryPoint
class LogoutDialog : RoundedBottomSheetDialog() {


    private var binding_: LogoutDialogBinding? = null
    private val binding get() = binding_!!


    private val viewModel by viewModels<LogoutDialogViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding_ = LogoutDialogBinding.inflate(inflater, container, false).apply {
            setLifecycleOwner { viewLifecycleOwner.lifecycle }
        }
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.viewState.collectLatest { state ->
                        handleViewState(state)
                    }
                }
            }
        }

        binding.logoutBtn.setOnClickListener {
            //
            handleLogout()
        }
        binding.cancelBtn.setOnClickListener {
            //
            if (!isCancelable) return@setOnClickListener
            dismiss()
        }



    }

    private fun handleViewState(state: LogoutDialogState) {
        hideProgressbar()
        when (state) {
            is LogoutDialogState.LogoutSuccess -> {
                startActivity(SplashActivity::class.java)
                requireActivity().finish()
            }
            is LogoutDialogState.LogoutFailed -> {
                isCancelable = true
                errorSnackbar(
                    binding.root,
                    message = state.response?.errorsResponse?.message ?: state.message ?: getString(
                        R.string.staticErrorMessage
                    ),
                    duration = Snackbar.LENGTH_LONG,
                    action = "OK"
                )
            }
        }
    }

    private fun hideProgressbar() {
        binding.progressbar.isVisible = false
        binding.logoutBtn.isVisible = true
    }

    private fun handleLogout() {
        isCancelable = false
        binding.apply {
            logoutBtn.isVisible = false
            progressbar.isVisible = true
        }
        viewModel.handleEvent(event = LogoutDialogEvent.Logout)
    }


    override fun onDestroyView() {
        binding_ = null
        super.onDestroyView()
    }


}