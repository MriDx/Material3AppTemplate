package com.mridx.androidtemplate.domain.repository.base

import com.google.gson.Gson
import com.mridx.androidtemplate.data.remote.model.misc.ErrorResponse
import retrofit2.Response
import com.mridx.androidtemplate.data.remote.model.utils.Resource

interface BaseRepository {

    //suspend fun <ResponseIn, ResponseOut> parseResponse(response: Response<ResponseIn>): Resource<ResponseOut>

    suspend fun <T> parseError(response: Response<T>, gson: Gson): ErrorResponse {
        return gson.fromJson(response.errorBody()?.charStream(), ErrorResponse::class.java)
    }

}