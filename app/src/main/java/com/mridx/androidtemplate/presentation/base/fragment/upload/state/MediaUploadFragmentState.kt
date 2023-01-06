package com.mridx.androidtemplate.presentation.base.fragment.upload.state

import com.mridx.androidtemplate.data.remote.model.misc.FileUploadModel
import com.mridx.androidtemplate.data.remote.model.utils.Resource
import com.mridx.androidtemplate.domain.use_case.location.GpsResult

sealed class MediaUploadFragmentState {

    data class UploadSuccess(
        val tmpUrl: String
    ) : MediaUploadFragmentState()

    data class UploadFailed(
        val response: Resource<FileUploadModel>? = null,
        val errorMessage: String?
    ) : MediaUploadFragmentState()


    data class GPSNotEnabled(val gpsResult: GpsResult) : MediaUploadFragmentState()

}
