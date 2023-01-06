package com.mridx.androidtemplate.presentation.user.fragment.profile.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.mridx.androidtemplate.R
import com.mridx.androidtemplate.databinding.HorizontalInfoItemViewBinding
import com.mridx.androidtemplate.databinding.ProfileFragmentBinding
import com.mridx.androidtemplate.presentation.app.fragment.home.event.HomeFragmentEvent
import com.mridx.androidtemplate.presentation.app.fragment.home.state.HomeFragmentState
import com.mridx.androidtemplate.presentation.app.fragment.home.vm.HomeFragmentViewModel
import com.mridx.androidtemplate.presentation.utils.PlaceHolderDrawableHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment() {


    private var binding_: ProfileFragmentBinding? = null
    private val binding get() = binding_!!


    private val viewModel by viewModels<HomeFragmentViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding_ = ProfileFragmentBinding.inflate(inflater, container, false).apply {
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
                launch {
                    viewModel.handleEvent(event = HomeFragmentEvent.FetchUser)
                }
            }
        }


    }

    private fun handleState(state: HomeFragmentState) {
        when (state) {
            is HomeFragmentState.UserFetched -> {
                renderUserDetails(state)
            }
            else -> {

            }
        }
    }

    private fun renderUserDetails(state: HomeFragmentState.UserFetched) {
        binding.apply {
            Glide.with(requireContext())
                .asBitmap()
                .load(state.userModel.photo)
                .placeholder(
                    PlaceHolderDrawableHelper.getAvatar(
                        requireContext(),
                        state.userModel.name,
                        0
                    )
                )
                .into(avatarView)
            userNameView.text = state.userModel.name.replaceFirstChar { it.uppercase() }
            //userInfoView.text = state.userModel.role.replaceFirstChar { it.uppercase() }
            userInfoView.text = state.userModel.designation.split("_")
                .joinToString(separator = " ") { f -> f.replaceFirstChar { i -> i.uppercase() } }


            profileInfoView.removeAllViews()
            val detailsMap = state.userModel.getMapForProfile()

            detailsMap.entries.forEachIndexed { index, item ->
                val itemView = DataBindingUtil.inflate<HorizontalInfoItemViewBinding>(
                    LayoutInflater.from(requireContext()),
                    R.layout.horizontal_info_item_view,
                    profileInfoView,
                    false
                ).apply {
                    headingView.text = item.key
                    valueView.text = item.value
                    hrView.isVisible = index.plus(1) != detailsMap.size
                }.root
                profileInfoView.addView(itemView)
            }


        }
    }



    override fun onDestroyView() {
        binding_ = null
        super.onDestroyView()
    }


}