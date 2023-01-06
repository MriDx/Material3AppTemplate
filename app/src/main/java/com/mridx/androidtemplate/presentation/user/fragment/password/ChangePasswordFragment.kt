package com.mridx.androidtemplate.presentation.user.fragment.password

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.mridx.androidtemplate.databinding.ChangePasswordBinding
import com.mridx.androidtemplate.presentation.dialog.bottom_message.BottomMessageDialog
import com.mridx.androidtemplate.presentation.user.fragment.password.vm.ChangePasswordFragmentViewModel
import com.mridx.androidtemplate.utils.errorSnackbar
import com.sumato.ino.officer.presentation.user.password.event.ChangePasswordFragmentEvent
import com.mridx.androidtemplate.presentation.user.fragment.password.state.ChangePasswordFragmentState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChangePasswordFragment : Fragment() {


    private var binding_: ChangePasswordBinding? = null
    private val binding get() = binding_!!

    private val viewModel by viewModels<ChangePasswordFragmentViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding_ = ChangePasswordBinding.inflate(inflater, container, false).apply {
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
                        handleState(state)
                    }
                }
            }
        }


        binding.submitBtn.setOnClickListener {
            handleSubmitBtn()
        }

        binding.passwordField.doAfterTextChanged {
            binding.passwordFieldLayout.error = null
        }
        binding.newPasswordField.doAfterTextChanged {
            binding.newPasswordFieldLayout.error = null
        }
        binding.confirmNewPasswordField.doAfterTextChanged {
            binding.confirmPasswordFieldLayout.error = null
        }

    }

    private fun handleState(state: ChangePasswordFragmentState) {
        when (state) {
            is ChangePasswordFragmentState.Response -> {
                handlePasswordChangeResponse(state)
            }
        }
    }

    private fun handlePasswordChangeResponse(state: ChangePasswordFragmentState.Response) {
        binding.submitBtn.showProgressbar(false)

        if (state.response?.data?.errorsResponse != null) {
            val errors = state.response.data?.errorsResponse?.errors
            if (errors != null && errors.isJsonObject) {
                renderInlineErrors(errors.asJsonObject)
                return
            }
            errorSnackbar(
                binding.root,
                message = state.response.data?.errorsResponse?.message ?: state.response.message
                ?: "Could not change your password at this moment. Please try later.",
                duration = Snackbar.LENGTH_INDEFINITE,
                action = "OK"
            )
            return
        }

        BottomMessageDialog.Builder()
            .setMessage(message = "Password changed successfully !")
            .setOnDone { d ->
                d.dismiss()
                findNavController().popBackStack()
            }
            .show(childFragmentManager, "")

    }

    private fun renderInlineErrors(errors: JsonObject) {
        errors.keySet().forEach { key ->
            when (key) {
                "current_password" -> {
                    binding.passwordFieldLayout.error = errors[key].asJsonArray[0].asString
                }
                "password" -> {
                    binding.newPasswordFieldLayout.error = errors[key].asJsonArray[0].asString
                }
                "password_confirmation" -> {
                    binding.confirmPasswordFieldLayout.error = errors[key].asJsonArray[0].asString
                }
            }
        }
    }


    private fun handleSubmitBtn() {
        val currentPassword = binding.passwordField.text.toString().trim()
        val newPassword = binding.newPasswordField.text.toString().trim()
        val confirmNewPassword = binding.confirmNewPasswordField.text.toString().trim()

        val event = ChangePasswordFragmentEvent.Change(
            currentPassword = currentPassword,
            newPassword = newPassword,
            newConfirmationPassword = confirmNewPassword
        )

        if (!validateForm(event)) {
            binding.submitBtn.showProgressbar(false)
            return
        }

        viewModel.handleEvent(event = event)

    }

    private fun validateForm(event: ChangePasswordFragmentEvent.Change): Boolean {
        var valid = true

        val errors = JsonObject()

        if (event.currentPassword.isEmpty()) {
            errors.add("current_password", JsonArray().apply {
                add("Current password is empty.")
            })
            //binding.passwordFieldLayout.error = "Current password is empty."
            valid = false
        }
        if (event.newPassword.length < 8) {
            errors.add("password", JsonArray().apply {
                add("New password must be at-least 8 character.")
            })
            //binding.newPasswordFieldLayout.error = "New password must be at-least 8 character."
            valid = false
        }
        if (!event.newPassword.contentEquals(event.newConfirmationPassword, false)) {
            errors.add("password_confirmation", JsonArray().apply {
                add("The password confirmation does not match.")
            })
            //binding.confirmPasswordFieldLayout.error = "The password confirmation does not match."
            valid = false
        }

        if (!valid) {
            renderInlineErrors(errors)
        }

        return valid
    }


    override fun onDestroyView() {
        binding_ = null
        super.onDestroyView()
    }

}