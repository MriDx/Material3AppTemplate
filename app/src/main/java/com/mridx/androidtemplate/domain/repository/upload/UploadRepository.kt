package com.mridx.androidtemplate.domain.repository.upload

import com.mridx.androidtemplate.data.remote.model.misc.FileUploadModel
import com.mridx.androidtemplate.data.remote.model.utils.Resource

interface UploadRepository {

    suspend fun uploadFile(filePath: String): Resource<FileUploadModel>

}