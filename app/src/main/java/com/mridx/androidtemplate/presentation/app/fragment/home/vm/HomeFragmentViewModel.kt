package com.mridx.androidtemplate.presentation.app.fragment.home.vm

import androidx.lifecycle.viewModelScope
import com.mridx.androidtemplate.data.remote.model.home.HomePageModel
import com.mridx.androidtemplate.domain.use_case.home.GetHomePageContentsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.mridx.androidtemplate.domain.use_case.user.GetLocalUserUseCase
import com.mridx.androidtemplate.presentation.app.fragment.home.event.HomeFragmentEvent
import com.mridx.androidtemplate.presentation.app.fragment.home.state.HomeFragmentState
import com.mridx.androidtemplate.presentation.base.vm.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class HomeFragmentViewModel @Inject constructor(
    private val fetchLocalUserUseCase: GetLocalUserUseCase,
    private val getHomePageContentsUseCase: GetHomePageContentsUseCase,
) : BaseViewModel<HomeFragmentEvent, HomeFragmentState>() {


    override fun handleEvent(event: HomeFragmentEvent) {
        when (event) {
            is HomeFragmentEvent.FetchUser -> {
                fetchLocalUser()
            }
            is HomeFragmentEvent.FetchContents -> {
                fetchHomePageContents(event)
            }
        }
    }

    private fun fetchHomePageContents(event: HomeFragmentEvent.FetchContents) {
        viewModelScope.launch(Dispatchers.IO) {

            val response = getHomePageContentsUseCase.execute()

            sendState(state = HomeFragmentState.HomePageContents(homePageModel = response))

        }
    }


    private fun fetchLocalUser() {
        viewModelScope.launch(Dispatchers.IO) {
            val userModel = fetchLocalUserUseCase()

            sendState(state = HomeFragmentState.UserFetched(userModel))

        }
    }


}