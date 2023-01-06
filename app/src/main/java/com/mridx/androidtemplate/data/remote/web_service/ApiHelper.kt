package com.mridx.androidtemplate.data.remote.web_service

import com.google.gson.JsonElement
import com.mridx.androidtemplate.data.remote.model.auth.AuthResponseModel
import com.mridx.androidtemplate.data.remote.model.misc.FileUploadModel
import com.mridx.androidtemplate.data.remote.model.misc.ResponseModel
import com.mridx.androidtemplate.data.remote.model.user.UserDetailsModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

interface ApiHelper {


    suspend fun login(requestBody: RequestBody): Response<AuthResponseModel>

    suspend fun logout(): Response<ResponseModel<JsonElement>>

    suspend fun getUserDetails(headers: Map<String, String>): Response<ResponseModel<UserDetailsModel>>

    suspend fun getUserDetails(): Response<ResponseModel<UserDetailsModel>>

    suspend fun uploadFile(body: MultipartBody.Part): Response<FileUploadModel>

    suspend fun changeUserPassword(body: RequestBody): Response<ResponseModel<JsonElement>>

}