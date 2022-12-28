package com.mridx.androidtemplate.presentation.app.fragment.home.event

sealed class HomeFragmentEvent {

    object FetchUser : HomeFragmentEvent()

    object FetchContents: HomeFragmentEvent()

}
