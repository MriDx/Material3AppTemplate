package com.mridx.androidtemplate.data.remote.web_service

import com.google.gson.JsonElement
import com.mridx.androidtemplate.data.remote.model.auth.AuthResponseModel
import com.mridx.androidtemplate.data.remote.model.misc.FileUploadModel
import com.mridx.androidtemplate.data.remote.model.misc.ResponseModel
import com.mridx.androidtemplate.data.remote.model.user.UserDetailsModel
import com.mridx.androidtemplate.utils.GsonInterface
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {


    @GsonInterface
    @POST("login")
    suspend fun login(@Body requestBody: RequestBody): Response<AuthResponseModel>

    @GsonInterface
    @POST("logout")
    suspend fun logout(): Response<ResponseModel<JsonElement>>

    @GsonInterface
    @GET("me")
    suspend fun getUserDetails(@HeaderMap headers: Map<String, String>): Response<ResponseModel<UserDetailsModel>>

    @GsonInterface
    @GET("me")
    suspend fun getUserDetails(): Response<ResponseModel<UserDetailsModel>>


    @GsonInterface
    @POST("update-password")
    suspend fun changeUserPassword(
        @Body body: RequestBody
    ): Response<ResponseModel<JsonElement>>


    @GsonInterface
    @Multipart
    @POST("uploads")
    suspend fun uploadFile(@Part body: MultipartBody.Part): Response<FileUploadModel>


}