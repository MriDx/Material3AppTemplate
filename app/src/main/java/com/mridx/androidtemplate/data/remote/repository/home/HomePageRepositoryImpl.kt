package com.mridx.androidtemplate.data.remote.repository.home

import com.mridx.androidtemplate.data.remote.model.home.HomePageModel
import com.mridx.androidtemplate.data.remote.web_service.ApiHelper
import com.mridx.androidtemplate.domain.repository.home.HomePageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class HomePageRepositoryImpl @Inject constructor(
    private val apiHelper: ApiHelper,
) : HomePageRepository {

    override suspend fun getHomePageContents(): HomePageModel {

        return withContext(Dispatchers.IO) {

            val homePageModel = HomePageModel()

            try {

            } catch (e: Exception) {
                e.printStackTrace()
            }

            homePageModel

        }

    }

}