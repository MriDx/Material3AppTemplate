package com.mridx.androidtemplate.domain.repository.home

import com.mridx.androidtemplate.data.remote.model.home.HomePageModel

interface HomePageRepository {

    suspend fun getHomePageContents(): HomePageModel

}