package com.mridx.androidtemplate.domain.use_case.home

import com.mridx.androidtemplate.data.remote.model.home.HomePageModel
import com.mridx.androidtemplate.domain.repository.home.HomePageRepository
import javax.inject.Inject

class GetHomePageContentsUseCase @Inject constructor(
    private val homePageRepository: HomePageRepository
) {

    suspend fun execute(): HomePageModel {
        return homePageRepository.getHomePageContents()
    }

}