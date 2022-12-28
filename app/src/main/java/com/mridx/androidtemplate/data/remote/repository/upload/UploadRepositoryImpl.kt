package com.mridx.androidtemplate.data.remote.repository.upload

import com.google.gson.Gson
import com.mridx.androidtemplate.data.remote.model.misc.ErrorResponse
import com.mridx.androidtemplate.data.remote.model.misc.FileUploadModel
import com.mridx.androidtemplate.data.remote.model.utils.Resource
import com.mridx.androidtemplate.data.remote.web_service.ApiHelper
import com.mridx.androidtemplate.domain.repository.upload.UploadRepository
import com.mridx.androidtemplate.utils.parseException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileNotFoundException
import javax.inject.Inject

class UploadRepositoryImpl @Inject constructor(
    private val apiHelper: ApiHelper,
    private val gson: Gson,
)  : UploadRepository {



    override suspend fun uploadFile(filePath: String): Resource<FileUploadModel> {
        return withContext(Dispatchers.IO) {

            try {
                val file = File(filePath)
                if (!file.exists()) throw FileNotFoundException("file not found")
                val requestFile = file.asRequestBody("application/octet-stream".toMediaType())
                val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

                val response = apiHelper.uploadFile(body = body)

                if (!response.isSuccessful) {
                    val error =
                        gson.fromJson(response.errorBody()?.charStream(), ErrorResponse::class.java)
                    throw Exception(
                        error.message ?: "Something went wrong ! Please try again after some time."
                    )
                }

                Resource.success(data = response.body()!!)

            } catch (e: Exception) {
                e.printStackTrace()
                Resource.error(message = parseException(e))
            }
        }
    }


}