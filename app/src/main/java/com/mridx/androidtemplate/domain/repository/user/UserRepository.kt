package com.mridx.androidtemplate.domain.repository.user

import com.google.gson.JsonElement
import org.json.JSONObject
import com.mridx.androidtemplate.data.local.model.user.UserModel
import com.mridx.androidtemplate.data.remote.model.auth.AuthResponseModel
import com.mridx.androidtemplate.data.remote.model.misc.ResponseModel
import com.mridx.androidtemplate.data.remote.model.user.LoginResponseModel
import com.mridx.androidtemplate.data.remote.model.user.UserDetailsModel
import com.mridx.androidtemplate.data.remote.model.utils.Resource
import com.mridx.androidtemplate.domain.repository.base.BaseRepository

interface UserRepository : BaseRepository {


    suspend fun login(params: JSONObject): Resource<ResponseModel<AuthResponseModel>>

    suspend fun storeUser(userDetailsModel: UserDetailsModel)

    suspend fun fetchLocalUser(): UserModel

    suspend fun logout(): Resource<ResponseModel<JsonElement>>

    suspend fun clearUserData()

    suspend fun getUserDetails(authToken: String): Resource<ResponseModel<UserDetailsModel>>

    suspend fun getUserDetails(): Resource<ResponseModel<UserDetailsModel>>

    suspend fun changePassword(params: JSONObject): Resource<ResponseModel<JsonElement>>

}