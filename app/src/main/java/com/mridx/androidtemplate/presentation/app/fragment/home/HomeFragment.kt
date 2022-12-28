package com.mridx.androidtemplate.presentation.app.fragment.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.mridx.androidtemplate.databinding.HomeFragmentBinding
import com.mridx.androidtemplate.presentation.app.fragment.home.event.HomeFragmentEvent
import com.mridx.androidtemplate.presentation.app.fragment.home.state.HomeFragmentState
import com.mridx.androidtemplate.presentation.app.fragment.home.vm.HomeFragmentViewModel
import com.mridx.androidtemplate.presentation.utils.PlaceHolderDrawableHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var binding_: HomeFragmentBinding? = null
    private val binding get() = binding_!!

    private val viewModel by viewModels<HomeFragmentViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding_ = HomeFragmentBinding.inflate(inflater, container, false).apply {
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
                launch {
                    viewModel.handleEvent(event = HomeFragmentEvent.FetchUser)
                }
                launch {
                    withContext(Dispatchers.Main) {
                        binding.swipeRefreshLayout.isRefreshing = true
                    }
                    viewModel.handleEvent(event = HomeFragmentEvent.FetchContents)
                }
            }
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            //
            viewModel.handleEvent(event = HomeFragmentEvent.FetchContents)
        }


    }

    private fun handleViewState(state: HomeFragmentState) {
        when (state) {
            is HomeFragmentState.UserFetched -> {
                renderUserDetails(state)
            }
            is HomeFragmentState.HomePageContents -> {
                handleHomePageContents(state)
            }
        }
    }

    private fun handleHomePageContents(state: HomeFragmentState.HomePageContents) {
        binding.swipeRefreshLayout.isRefreshing = false
    }


    private fun renderUserDetails(state: HomeFragmentState.UserFetched) {
        Glide.with(requireContext()).asBitmap().load(state.userModel.photo).placeholder(
            PlaceHolderDrawableHelper.getAvatar(
                requireContext(), state.userModel.name, 0
            )
        ).into(binding.avatar)

        binding.userNameView.text = state.userModel.name
        binding.designationView.text = state.userModel.designation

        binding.userInfoCard.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToSettingsFragment())
        }

    }


    override fun onDestroyView() {
        binding_ = null
        super.onDestroyView()
    }

}