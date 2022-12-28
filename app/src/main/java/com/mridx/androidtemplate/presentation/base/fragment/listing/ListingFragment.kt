package com.mridx.androidtemplate.presentation.base.fragment.listing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mridx.androidtemplate.data.local.model.listing.UIError
import com.mridx.androidtemplate.databinding.ListingFragmentBinding
import com.mridx.androidtemplate.presentation.base.vm.ListingViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

abstract class ListingFragment : Fragment() {

    private var binding_: ListingFragmentBinding? = null
    private val binding get() = binding_!!

    val getParentView get() = binding.root


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding_ = ListingFragmentBinding.inflate(inflater, container, false).apply {
            setLifecycleOwner { viewLifecycleOwner.lifecycle }
            viewModel = this@ListingFragment.getViewModel()
        }
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setTitle(getTitle())

        binding.searchIcon.isVisible = showSearch()
        //binding.filterBtn.isVisible = showFilter()

        binding.itemHolder.apply {
            adapter = this@ListingFragment.getAdapter()
            layoutManager = this@ListingFragment.getLayoutManager()
        }


        binding.searchIcon.setOnClickListener {
            onSearchAction()
        }

        binding.swipeRefreshLayout.setOnRefreshListener { onRefreshing() }


        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    getViewModel<ListingViewModel<Any, Any>>()?.errorState?.collectLatest {
                        handleErrorState(it)
                    }
                }
            }
        }


    }

    private fun handleErrorState(error: UIError) {
        binding.itemHolder.isVisible = !error.showError
        binding.errorViewHolder.isVisible = error.showError
        binding.textView.text = error.errorMessage
    }


    open fun <T> getViewModel(): T? {
        return null
    }

    open fun onSearchAction() {

    }


    open fun <T> getAdapter(): T? {
        return null
    }

    open fun getLayoutManager(): RecyclerView.LayoutManager {
        return LinearLayoutManager(requireContext())
    }

    /*open fun showEmptyList(show: Boolean, message: String? = "No item found !") {
        //for show == true show empty list
        binding.apply {
            errorUI.apply {
                textView.text = message
            }.root.isVisible = show
            itemHolder.isVisible = !show
        }
    }*/

    /*open fun showError(message: String? = null, show: Boolean = true) {
        //
        binding.apply {
            errorUI.apply {
                textView.text = message
            }.root.isVisible = show
            itemHolder.isVisible = !show
        }
    }*/

    open fun onRefreshing() {}

    open fun setRefreshing(isRefreshing: Boolean = false) {
        binding.swipeRefreshLayout.isRefreshing = isRefreshing
    }

    open fun hideProgressbar() {
        binding.progressbar.isVisible = false
    }

    fun setTitle(title: String) {
        binding.heading.text = title
    }

    open fun getTitle(): String = "Demo Listing"

    open fun searchIcon(show: Boolean = false) {
        binding.searchIcon.isVisible = false
    }

    open fun showSearch() = true

    open fun showFilter() = true


    override fun onDestroyView() {
        binding_ = null
        super.onDestroyView()
    }
}