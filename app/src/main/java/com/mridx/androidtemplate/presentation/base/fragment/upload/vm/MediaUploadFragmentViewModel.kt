package com.mridx.androidtemplate.presentation.base.fragment.upload.vm

import android.content.SharedPreferences
import android.location.Location
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.mridx.androidtemplate.data.remote.model.utils.Resource
import com.mridx.androidtemplate.di.qualifier.AppPreference
import com.mridx.androidtemplate.domain.use_case.location.EnableGpsUseCase
import com.mridx.androidtemplate.domain.use_case.location.LocationUpdatesUseCase
import com.mridx.androidtemplate.domain.use_case.upload.UploadFileUseCase
import com.mridx.androidtemplate.presentation.base.vm.BaseViewModel
import com.mridx.androidtemplate.utils.USER_NAME
import com.mridx.androidtemplate.utils.parseException
import com.mridx.androidtemplate.utils.storeImage
import com.mridx.watermarkdialog.Data
import com.mridx.watermarkdialog.Processor
import com.mridx.androidtemplate.presentation.base.fragment.upload.event.MediaUploadFragmentEvent
import com.mridx.androidtemplate.presentation.base.fragment.upload.state.MediaUploadFragmentState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.concurrent.CancellationException
import javax.inject.Inject

@HiltViewModel
open class MediaUploadFragmentViewModel @Inject constructor(
    private val enableGpsUseCase: EnableGpsUseCase,
    private val locationUpdatesUseCase: LocationUpdatesUseCase,
    private val uploadFileUseCase: UploadFileUseCase,
    @AppPreference private val sharedPreferences: SharedPreferences,
) : BaseViewModel<MediaUploadFragmentEvent, MediaUploadFragmentState>() {


    private var currentLocation: Location? = null

    private var locationUpdatesJob: Job? = null


    override fun handleEvent(event: MediaUploadFragmentEvent) {
        when (event) {
            is MediaUploadFragmentEvent.EnableGPS -> {
                //
                enableGPS()
            }
            is MediaUploadFragmentEvent.StartLocationUpdates -> {
                startLocationUpdates()
            }
            is MediaUploadFragmentEvent.Upload -> {
                uploadMedia(event)
            }
        }
    }

    private fun startLocationUpdates() {
        locationUpdatesJob?.cancel(CancellationException("Staring another request !"))
        locationUpdatesJob = viewModelScope.launch(Dispatchers.IO) {

            locationUpdatesUseCase.fetchUpdates().collectLatest { location ->
                currentLocation = location
                Log.d("mridx", "startLocationUpdates: ${location.latitude} ${location.longitude}")
            }

        }
    }

    private fun enableGPS() {
        viewModelScope.launch(Dispatchers.IO) {

            enableGpsUseCase().collectLatest { gpsResult ->
                if (gpsResult.enabled) {
                    //
                } else {
                    //
                    sendState(state = MediaUploadFragmentState.GPSNotEnabled(gpsResult = gpsResult))
                }
            }

        }
    }


    private fun uploadMedia(event: MediaUploadFragmentEvent.Upload) {
        viewModelScope.launch(Dispatchers.IO) {

            try {
                val processedImagePath =
                    processImage(event) ?: throw Exception("Could not process file !")

                val uploadResponse = uploadFileUseCase.execute(processedImagePath.path)

                val state = if (uploadResponse.isFailed()) {
                    MediaUploadFragmentState.UploadFailed(
                        response = uploadResponse,
                        errorMessage = uploadResponse.message
                    )
                } else {
                    MediaUploadFragmentState.UploadSuccess(
                        tmpUrl = uploadResponse.data!!.url
                    )
                }

                sendState(state)


            } catch (e: Exception) {
                e.printStackTrace()
                sendState(
                    state = MediaUploadFragmentState.UploadFailed(
                        response = Resource.error(message = parseException(e)),
                        errorMessage = parseException(e)
                    )
                )
            }


        }

    }

    private suspend fun processImage(event: MediaUploadFragmentEvent.Upload): File? {
        return withContext(Dispatchers.IO) {
            val imageFile = File(event.filePath)
            if (!imageFile.exists()) {
                //return error
                return@withContext null
            }

            val watermarks = mutableMapOf(
                "Location" to "${currentLocation?.latitude ?: ""}/${currentLocation?.longitude ?: ""}",
                "Uploaded by" to (sharedPreferences.getString(USER_NAME, "") ?: ""),
            ).also { parentMap ->
                event.waterMarks.forEach {
                    parentMap[it.key] = it.value
                }
            }

            val processedImageBitmap = Processor.process(
                file = imageFile,
                maxHeight = 720f,
                maxWidth = 720f,
                waterMarkData = Data.WaterMarkData(
                    waterMarks = watermarks,
                    position = Data.WaterMarkPosition.BOTTOM_LEFT
                ),
                typeface = event.typeface
            ) ?: throw Exception("Image could not be processed.")

            val uploadableFile = File(event.processedImagePath)

            val processedImage = storeImage(
                image = processedImageBitmap,
                pictureFile = uploadableFile
            ) ?: throw Exception("Could not store processed image.")

            processedImage
        }
    }


}