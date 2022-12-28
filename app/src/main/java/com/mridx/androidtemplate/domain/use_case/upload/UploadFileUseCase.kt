package com.mridx.androidtemplate.domain.use_case.upload

import com.mridx.androidtemplate.data.remote.model.misc.FileUploadModel
import com.mridx.androidtemplate.data.remote.model.utils.Resource
import com.mridx.androidtemplate.domain.repository.upload.UploadRepository
import javax.inject.Inject

class UploadFileUseCase @Inject constructor(
    private val uploadRepository: UploadRepository
) {

    suspend fun execute(filePath: String): Resource<FileUploadModel> {
        return uploadRepository.uploadFile(filePath)
    }

}