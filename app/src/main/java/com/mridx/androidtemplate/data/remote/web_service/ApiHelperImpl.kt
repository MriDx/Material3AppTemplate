package com.mridx.androidtemplate.data.remote.web_service

import com.google.gson.JsonElement
import com.mridx.androidtemplate.data.remote.model.auth.AuthResponseModel
import com.mridx.androidtemplate.data.remote.model.misc.FileUploadModel
import com.mridx.androidtemplate.data.remote.model.misc.ResponseModel
import com.mridx.androidtemplate.data.remote.model.user.UserDetailsModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import javax.inject.Inject

class ApiHelperImpl @Inject constructor(private val apiService: ApiService) : ApiHelper {


    override suspend fun login(requestBody: RequestBody): Response<AuthResponseModel> =
        apiService.login(requestBody)

    override suspend fun logout(): Response<ResponseModel<JsonElement>> = apiService.logout()

    override suspend fun getUserDetails(headers: Map<String, String>): Response<ResponseModel<UserDetailsModel>> =
        apiService.getUserDetails(headers)

    override suspend fun getUserDetails(): Response<ResponseModel<UserDetailsModel>> =
        apiService.getUserDetails()

    override suspend fun changeUserPassword(body: RequestBody): Response<ResponseModel<JsonElement>> =
        apiService.changeUserPassword(body)

    override suspend fun uploadFile(body: MultipartBody.Part): Response<FileUploadModel> =
        apiService.uploadFile(body)


}