package com.mridx.androidtemplate.presentation.base.fragment.upload.vm

import android.content.SharedPreferences
import android.location.Location
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.mridx.androidtemplate.data.local.model.media.ProcessImageModel
import com.mridx.androidtemplate.domain.use_case.location.EnableGpsUseCase
import com.mridx.androidtemplate.domain.use_case.location.LocationUpdatesUseCase
import com.mridx.androidtemplate.domain.use_case.upload.UploadFileUseCase
import com.mridx.androidtemplate.presentation.base.vm.BaseViewModel
import com.mridx.androidtemplate.utils.USER_NAME
import com.mridx.androidtemplate.utils.storeImage
import com.mridx.watermarkdialog.Data
import com.mridx.watermarkdialog.Processor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.concurrent.CancellationException

open class MediaUploadFragmentViewModelOne<Event, State> constructor(
    private val enableGpsUseCase: EnableGpsUseCase,
    private val locationUpdatesUseCase: LocationUpdatesUseCase,
    private val uploadFileUseCase: UploadFileUseCase,
    private val sharedPreferences: SharedPreferences,
) : BaseViewModel<Event, State>() {


    private var currentLocation: Location? = null

    private var locationUpdatesJob: Job? = null

    override fun handleEvent(event: Event) {

    }


    open fun startLocationUpdates() {
        locationUpdatesJob?.cancel(CancellationException("Staring another request !"))
        locationUpdatesJob = viewModelScope.launch(Dispatchers.IO) {

            locationUpdatesUseCase.fetchUpdates().collectLatest { location ->
                currentLocation = location
                Log.d("mridx", "startLocationUpdates: ${location.latitude} ${location.longitude}")
            }

        }
    }

    open fun enableGPS() {
       /* viewModelScope.launch(Dispatchers.IO) {

            enableGpsUseCase().collectLatest { gpsResult ->
                if (gpsResult.enabled) {
                    //
                } else {
                    //
                    sendState(state = MediaUploadFragmentState.GPSNotEnabled(gpsResult = gpsResult))
                }
            }
        }*/
    }


    open suspend fun processImage(processImageModel: ProcessImageModel): File? {
        return withContext(Dispatchers.IO) {
            val imageFile = File(processImageModel.filePath)
            if (!imageFile.exists()) {
                //return error
                return@withContext null
            }

            val watermarks = mutableMapOf(
                "Location" to "${currentLocation?.latitude ?: ""}/${currentLocation?.longitude ?: ""}",
                "Uploaded by" to (sharedPreferences.getString(USER_NAME, "") ?: ""),
            ).also { parentMap ->
                processImageModel.waterMarks.forEach {
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
                typeface = processImageModel.typeface
            ) ?: throw Exception("Image could not be processed.")

            val uploadableFile = File(processImageModel.processedImagePath)

            val processedImage = storeImage(
                image = processedImageBitmap,
                pictureFile = uploadableFile
            ) ?: throw Exception("Could not store processed image.")

            processedImage
        }
    }


}