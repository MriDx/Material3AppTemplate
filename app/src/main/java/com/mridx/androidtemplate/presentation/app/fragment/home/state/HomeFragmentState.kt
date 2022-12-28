package com.mridx.androidtemplate.presentation.app.fragment.home.state

import com.mridx.androidtemplate.data.local.model.user.UserModel
import com.mridx.androidtemplate.data.remote.model.home.HomePageModel

sealed class HomeFragmentState {

    data class UserFetched(val userModel: UserModel) : HomeFragmentState()

    data class HomePageContents(val homePageModel: HomePageModel): HomeFragmentState()

}