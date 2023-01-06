package com.mridx.androidtemplate.presentation.app.fragment.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import com.mridx.androidtemplate.R
import com.mridx.androidtemplate.data.local.constants.settings_fragment.SettingsItemModel
import com.mridx.androidtemplate.databinding.DividerItemViewBinding
import com.mridx.androidtemplate.databinding.SettingsFragmentBinding
import com.mridx.androidtemplate.databinding.SettingsItemViewBinding
import com.mridx.androidtemplate.di.qualifier.BaseUrl
import com.mridx.androidtemplate.di.qualifier.SettingsItems
import com.mridx.androidtemplate.presentation.dialog.bottom_message.BottomMessageDialog
import com.sumato.ino.officer.data.local.model.web_view.WebViewModel
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private var binding_: SettingsFragmentBinding? = null
    private val binding get() = binding_!!

    @Inject
    @SettingsItems
    lateinit var settingsItems: List<SettingsItemModel>

    @Inject
    @BaseUrl
    lateinit var baseUrl: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding_ = SettingsFragmentBinding.inflate(inflater, container, false).apply {
            setLifecycleOwner { viewLifecycleOwner.lifecycle }
        }
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        renderSettingsItems()


    }

    private fun renderSettingsItems() {
        binding.settingsGroupOne.apply {
            setItemCount(settingsItems.size)
            itemBuilder = { parent, index ->
                val item = settingsItems[index]
                if (item.isBlank()) {
                    DataBindingUtil.inflate<DividerItemViewBinding>(
                        LayoutInflater.from(parent.context),
                        R.layout.divider_item_view,
                        parent,
                        false
                    ).root
                } else {
                    DataBindingUtil.inflate<SettingsItemViewBinding>(
                        LayoutInflater.from(parent.context),
                        R.layout.settings_item_view,
                        parent,
                        false
                    ).root
                }
            }
            itemBinding { holder, index ->
                val item = settingsItems[index]
                if (!item.isBlank()) {
                    DataBindingUtil.bind<SettingsItemViewBinding>(holder.itemView)?.apply {
                        headingView.text = getString(item.heading)
                        iconView.setImageResource(item.icon)
                        if (item.hasSubHeading()) {
                            subHeadingView.text =
                                if (item.subHeading.isNullOrBlank()) getString(item.subHeadingRes) else item.subHeading
                        }
                        subHeadingView.isVisible = item.hasSubHeading()
                        root.setOnClickListener {
                            handleActionItemClicked(item)
                        }
                    }
                }
            }
        }.render()
    }

    private fun handleActionItemClicked(item: SettingsItemModel) {
        when (item.actionId) {
            "profile" -> {
                findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToProfileFragment())
            }
            "logout" -> {
                //show logout confirmation button
                findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToLogoutDialog())
            }
            "privacy_policy" -> {
                findNavController().navigate(
                    SettingsFragmentDirections.actionSettingsFragmentToWebViewFragment(
                        title = getString(R.string.settingsFragmentPrivacyPolicy),
                        webViewModel = WebViewModel(url = "${baseUrl}privacy-policy")
                    )
                )
            }
        }
    }


    override fun onDestroyView() {
        binding_ = null
        super.onDestroyView()
    }

}