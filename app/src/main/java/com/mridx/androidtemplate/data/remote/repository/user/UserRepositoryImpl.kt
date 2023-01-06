package com.mridx.androidtemplate.data.remote.repository.user

import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.common.collect.ImmutableMap
import com.google.gson.Gson
import com.google.gson.JsonElement
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.Response
import com.mridx.androidtemplate.data.local.model.user.UserModel
import com.mridx.androidtemplate.data.remote.model.auth.AuthResponseModel
import com.mridx.androidtemplate.data.remote.model.misc.ErrorResponse
import com.mridx.androidtemplate.data.remote.model.misc.ResponseModel
import com.mridx.androidtemplate.data.remote.model.user.LoginResponseModel
import com.mridx.androidtemplate.data.remote.model.user.UserDetailsModel
import com.mridx.androidtemplate.data.remote.model.utils.Resource
import com.mridx.androidtemplate.data.remote.web_service.ApiHelper
import com.mridx.androidtemplate.di.qualifier.AppPreference
import com.mridx.androidtemplate.domain.repository.user.UserRepository
import com.mridx.androidtemplate.utils.*
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val apiHelper: ApiHelper,
    private val gson: Gson,
    @AppPreference private val sharedPreferences: SharedPreferences,
) : UserRepository {


    /**
     * login process
     *
     * 1. Log user with provided credentials
     * 2. on failed, return error resource
     * 3. on success, fetch user details with received auth token
     * 4. if user details failed to fetch, return errors from user details request
     * 5. otherwise save the auth token and continue with the execution.
     *
     */
    override suspend fun login(params: JSONObject): Resource<ResponseModel<AuthResponseModel>> {
        return withContext(Dispatchers.IO) {
            try {

                val requestBody = params.toRequestBody()

                val response = apiHelper.login(requestBody)

                if (!response.isSuccessful) {
                    val errorResponse =
                        gson.fromJson(response.errorBody()?.charStream(), ErrorResponse::class.java)
                    return@withContext Resource.error(
                        data = ResponseModel.withError(
                            errorResponse = errorResponse
                        ),
                        message = errorResponse?.message
                    )
                }



                sharedPreferences.edit {
                    putString(TOKEN, response.body()!!.getAccessToken())
                }

                val userResponse = apiHelper.getUserDetails()

                if (!userResponse.isSuccessful) {
                    //
                    sharedPreferences.edit {
                        clear()
                    }
                    val errorResponse =
                        gson.fromJson(
                            userResponse.errorBody()?.charStream(),
                            ErrorResponse::class.java
                        )
                    return@withContext Resource.error(
                        data = ResponseModel.withError(errorResponse = errorResponse),
                        message = errorResponse.message
                    )
                }

                storeUser(userDetailsModel = userResponse.body()!!.data!!)

                sharedPreferences.edit {
                    putString(TOKEN, response.body()!!.getAccessToken())
                }

                Resource.success(data = ResponseModel(data = response.body()!!))

            } catch (e: Exception) {
                e.printStackTrace()
                Resource.error(message = parseException(e))
            }
        }
    }

    override suspend fun storeUser(userDetailsModel: UserDetailsModel) {
        withContext(Dispatchers.IO) {
            sharedPreferences.edit {
                //putString(TOKEN, loginResponseModel.getAccessToken())
                putString(UUID, userDetailsModel.uuid)
                putString(USER_NAME, userDetailsModel.name)
                putString(USER_EMAIL, userDetailsModel.email)
                putString(DESIGNATION, userDetailsModel.designation)
                putString(PHONE, userDetailsModel.phone)
                putString(PHOTO, userDetailsModel.profile_url)
                putString(ROLE, userDetailsModel.role_name)
                putString(JOINED, userDetailsModel.date_of_joining)
                putString(GENDER, userDetailsModel.gender)
                putString(ADDRESS, userDetailsModel.address)
                putString(BIO, userDetailsModel.bio)
                putString(LAC, userDetailsModel.user_lac)
                putString(DIVISION, userDetailsModel.user_division)
                putString(NEW_DIVISION, userDetailsModel.newdivision_name)
                putString(WORKING_DIVISION, userDetailsModel.working_division)
                putBoolean(LOGGED_IN, true)
            }
        }
    }


    override suspend fun fetchLocalUser(): UserModel {
        return withContext(Dispatchers.IO) {
            UserModel.fromMap(sharedPreferences.all)
        }
    }


    override suspend fun logout(): Resource<ResponseModel<JsonElement>> {
        return withContext(Dispatchers.IO) {

            try {

                val response = apiHelper.logout()

                if (!response.isSuccessful) {
                    //
                    val errorResponse =
                        gson.fromJson(response.errorBody()?.charStream(), ErrorResponse::class.java)
                    return@withContext Resource.error(
                        data = ResponseModel.withError(
                            errorResponse = errorResponse
                        ),
                        message = errorResponse?.message
                    )
                }
                //clear user data
                clearUserData()
                Resource.success(
                    data = ResponseModel(
                        status = response.code(),
                        message = response.message()
                    )
                )

            } catch (e: Exception) {
                e.printStackTrace()
                Resource.error(message = parseException(e))
            }

        }
    }

    override suspend fun clearUserData() {
        withContext(Dispatchers.IO) {
            sharedPreferences.edit {
                clear()
            }
        }
    }

    override suspend fun getUserDetails(authToken: String): Resource<ResponseModel<UserDetailsModel>> {
        return withContext(Dispatchers.IO) {
            try {

                val response = apiHelper.getUserDetails(
                    headers = mapOf(
                        "Authorization" to "Bearer $authToken",
                        "Accept" to "application/json"
                    )
                )

                if (!response.isSuccessful) {
                    //
                    val errorResponse =
                        gson.fromJson(response.errorBody()?.charStream(), ErrorResponse::class.java)
                    return@withContext Resource.error(
                        data = ResponseModel.withError(errorResponse = errorResponse),
                        message = errorResponse.message
                    )
                }

                storeUser(userDetailsModel = response.body()!!.data!!)
                Resource.success(data = response.body())

            } catch (e: Exception) {
                e.printStackTrace()
                Resource.error(message = parseException(e))
            }
        }
    }

    override suspend fun getUserDetails(): Resource<ResponseModel<UserDetailsModel>> {
        return withContext(Dispatchers.IO) {
            try {

                val response = apiHelper.getUserDetails(mapOf())

                if (!response.isSuccessful) {
                    //
                    val errorResponse =
                        gson.fromJson(response.errorBody()?.charStream(), ErrorResponse::class.java)
                    return@withContext Resource.error(
                        data = ResponseModel.withError(errorResponse = errorResponse),
                        message = errorResponse.message
                    )
                }

                storeUser(userDetailsModel = response.body()!!.data!!)
                Resource.success(data = response.body())

            } catch (e: Exception) {
                e.printStackTrace()
                Resource.error(message = parseException(e))
            }
        }
    }

    override suspend fun changePassword(params: JSONObject): Resource<ResponseModel<JsonElement>> {
        return withContext(Dispatchers.IO) {
            try {

                val requestBody = params.toRequestBody()

                val response = apiHelper.changeUserPassword(requestBody)

                if (!response.isSuccessful) {
                    //
                    val errorResponse =
                        gson.fromJson(response.errorBody()?.charStream(), ErrorResponse::class.java)
                    return@withContext Resource.error(
                        data = ResponseModel.withError(errorResponse = errorResponse),
                        message = errorResponse.message
                    )
                }

                Resource.success(data = response.body())

            } catch (e: Exception) {
                e.printStackTrace()
                Resource.error(message = parseException(e))
            }
        }
    }


}