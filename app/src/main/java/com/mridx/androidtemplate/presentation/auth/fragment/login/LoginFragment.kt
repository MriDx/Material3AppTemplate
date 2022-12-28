package com.mridx.androidtemplate.presentation.auth.fragment.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import androidx.core.widget.doBeforeTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.mridx.androidtemplate.R
import com.mridx.androidtemplate.databinding.LoginFragmentBinding
import com.mridx.androidtemplate.presentation.auth.fragment.login.event.LoginFragmentEvent
import com.mridx.androidtemplate.presentation.auth.fragment.login.state.LoginFragmentState
import com.mridx.androidtemplate.presentation.auth.fragment.login.vm.LoginFragmentViewModel
import com.mridx.androidtemplate.utils.errorSnackbar

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var binding_: LoginFragmentBinding? = null
    private val binding get() = binding_!!


    private val viewModel by viewModels<LoginFragmentViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding_ = LoginFragmentBinding.inflate(inflater, container, false).apply {
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

        binding.welcomeText.text = buildSpannedString {
            append("Welcome \n")
            //bold { append("Login Here".uppercase()) }
            bold { append(getString(R.string.app_name)) }
        }

        binding.taglineText.text = "Building the nation with pride."

        //region To remove errors on starting of rewriting
        binding.loginField.doBeforeTextChanged { text, start, count, after ->
            binding.loginFieldLayout.error = null
        }
        binding.passwordField.doBeforeTextChanged { text, start, count, after ->
            binding.passwordFieldLayout.error = null
        }
        //endregion

        binding.loginBtn.setOnClickListener {
            //
            prepareForLogin()
        }

    }

    private fun prepareForLogin() {
        binding.loginBtn.showProgressbar(true)
        val login = binding.loginField.text.toString().trim()
        val password = binding.passwordField.text.toString().trim()

        val event = LoginFragmentEvent.LoginUser(
            login = login,
            password = password,
            deviceName = "app"
        )

        if (!validateForm(event)) {
            binding.loginBtn.showProgressbar(false)
            errorSnackbar(
                view = binding.root,
                message = "Please fill all fields properly.",
                duration = Snackbar.LENGTH_LONG,
                action = "OK"
            )
            return
        }

        viewModel.handleEvent(event)

    }

    private fun validateForm(event: LoginFragmentEvent.LoginUser): Boolean {
        var valid = true
        if (event.login.isEmpty()) {
            binding.loginFieldLayout.error = "Email or phone number is blank."
            valid = false
        }
        if (event.password.isEmpty()) {
            binding.passwordFieldLayout.error = "Password is blank."
            valid = false
        }
        return valid
    }


    private fun handleViewState(state: LoginFragmentState) {
        when (state) {
            is LoginFragmentState.LoginSuccess -> {
                binding.loginBtn.showProgressbar(false)
                findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToAppActivity())
                requireActivity().finish()
            }
            is LoginFragmentState.LoginFailed -> {
                binding.loginBtn.showProgressbar(false)
                // login failed, show errors
                handleLoginFailed(state)
            }
        }
    }

    private fun handleLoginFailed(state: LoginFragmentState.LoginFailed) {
        if (state.responseModel != null) {
            val errorsElement = state.responseModel.errorsResponse?.errors
            if (errorsElement != null && errorsElement.isJsonObject) {
                val errors = errorsElement.asJsonObject
                binding.loginFieldLayout.error = errors?.get("email")?.asJsonArray?.get(0)?.asString
                binding.passwordFieldLayout.error =
                    errors?.get("password")?.asJsonArray?.get(0)?.asString
            }
        }
        errorSnackbar(
            view = binding.root,
            message = state.responseModel?.errorsResponse?.message ?: state.message ?: getString(
                R.string.staticErrorMessage
            ),
            action = "OK",
            duration = Snackbar.LENGTH_INDEFINITE
        )

    }

    override fun onDestroyView() {
        binding_ = null
        super.onDestroyView()
    }

}