package com.sumato.ino.officer.presentation.app.fragment.media.upload

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.View
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.android.material.snackbar.Snackbar
import com.mridx.androidtemplate.BuildConfig
import com.mridx.androidtemplate.di.qualifier.PermissionPreference
import com.mridx.androidtemplate.domain.use_case.location.GpsResult
import com.mridx.androidtemplate.utils.*
import com.mridx.androidtemplate.presentation.image_cropper.activity.ImageCropActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*
import javax.inject.Inject


/**
 * MediaUploadFragment
 *
 * This file is base class for upload different types of media file.
 * This will handle required permission, image compressing etc.
 *
 */


@AndroidEntryPoint
open class MediaUploadFragment : Fragment() {


    @Inject
    @PermissionPreference
    lateinit var permissionPreferences: SharedPreferences


    private val requiredPermissions = mapOf(
        Permissions.CAMERA to Manifest.permission.CAMERA,
        Permissions.READ_STORAGE to Manifest.permission.READ_EXTERNAL_STORAGE,
        Permissions.WRITE_STORAGE to Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Permissions.LOCATION to Manifest.permission.ACCESS_FINE_LOCATION,
    )


    private val requestPermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { grantedPermissions ->
            val allPermissionGranted = grantedPermissions.values.all { it }
            if (!allPermissionGranted) {
                //ask to allow all permissions
                checkRequiredPermissions()
            } else {
                //continue image picker
                openImageChoiceDialog()
            }
        }

    /**
     * file path to capture and save file in case of camera capture.
     */
    private var currentFileToCapture: String? = null

    private val cameraCaptureLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { captured ->
            if (captured && currentFileToCapture != null) {
                /**
                 * if the image been captured successfully,
                 * will check if the file exists just to be safe.
                 * and will launch image cropping window if the captured file exists.
                 */
                //handle the file to show or manipulate
                val file = File(currentFileToCapture!!)
                if (file.exists()) {
                    imageCropFragmentLauncher.launch(file.toUri())
                }
            } else {
                view?.let {
                    infoSnackbar(
                        view = it,
                        message = "Could not capture image. Please check your permissions and try again later.",
                        duration = Snackbar.LENGTH_LONG,
                        action = "OK"
                    )
                }
            }
        }

    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) {
            imageCropFragmentLauncher.launch(it)
        }

    private val imageCropFragmentLauncher =
        registerForActivityResult(ImageCropActivity.CropImageActivityContract()) {
            if (it != null) {
                val file = File(it!!.path)
                compressImage(file)
            }
        }

    private val appSettingsLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            checkRequiredPermissions()
        }

    private val gpsExceptionResolverLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
            if (it.resultCode != Activity.RESULT_OK) {
                showEnableGPSService()
            }
        }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /**
         * as we need current location, its better to enable GPS service before anything.
         * override enableGPS function on your subclass.
         */
        enableGPS()

    }


    open fun enableGPS() {
        //viewModel.handleEvent(event = MediaUploadFragmentEvent.EnableGPS)
    }


    /**
     * In case the gps service is disabled, call this function.
     * This will try to solve by showing an user performable dialog to enable gps,
     * if possible; otherwise shows a dialog stating to enable GPS service manually.
     *
     */
    open fun handleLocationServiceDisableUseCase(gpsResult: GpsResult) {
        if (gpsResult.exception != null) {
            val statusCode = (gpsResult.exception as ApiException).statusCode
            when (statusCode) {
                LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                    (gpsResult.exception as? ResolvableApiException)?.resolution?.intentSender?.let {
                        gpsExceptionResolverLauncher.launch(IntentSenderRequest.Builder(it).build())
                    } ?: showEnableGPSService()
                }
                LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                    //open settings
                    showEnableGPSService()
                }
            }
        }
    }

    private fun showEnableGPSService() {
        showDialog(
            title = "Action Required !",
            message = "Please enable GPS Location service.",
            cancellable = false,
            showNegativeBtn = true,
            negativeBtn = "Cancel",
            positiveBtn = "OK"
        ) { d, i ->
            d.dismiss()
            if (i == POSITIVE_BTN) {
                enableGPS()
            } else {
                findNavController().popBackStack()
            }
        }.show()
    }




    /**
     * This will show a dialog listing the options.
     */
    private fun openImageChoiceDialog() {
        val imageChoices = arrayOf("Open Camera", "Select from Gallery")
        AlertDialog.Builder(requireContext()).setItems(
            imageChoices
        ) { dialog, i ->
            dialog.dismiss()
            when (i) {
                0 -> {
                    val fileToCapture = getTmpFileUri()
                    cameraCaptureLauncher.launch(fileToCapture)
                }
                1 -> {
                    imagePickerLauncher.launch("image/*")
                }
            }
        }.setPositiveButton("Cancel") { d, i ->
            d.dismiss()
        }.create().show()

        /**
         * starting location updates, on showing the choice dialog,
         * multiple calling will not cause any issue, as it will cancel previous updates.
         */
        startLocationUpdates()

    }

    open fun startLocationUpdates() {

    }


    /**
     * use this function to generate uri for capture image
     */
    private fun getTmpFileUri(): Uri {
        val tmpFile =
            File.createTempFile("tmp_image_file", ".png", requireContext().cacheDir).apply {
                createNewFile()
                deleteOnExit()
            }

        currentFileToCapture = tmpFile.path

        return getUriForFile(requireContext(), tmpFile)
    }


    /**
     * This will create a file to store the processed, compressed file as final to upload.
     * everytime it runs, it will remove all existing files under the directory.
     */
    open fun createDeletableUploadableFile(): File {
        val parentDir = File(
            requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "images/media/uploadable"
        )

        parentDir.deleteRecursively()
        parentDir.mkdirs()

        val file = File(parentDir, "${Date().time}.jpg")
        if (file.exists()) {
            file.delete()
        }
        file.createNewFile()
        return file
    }

    /**
     * This will create a file to store the file after cropping.
     * everytime it runs, it will remove all existing files under the directory.
     */
    private fun createImageFile(): File {
        val parentDir = File(
            requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "images/media/tmpStore"
        )

        parentDir.deleteRecursively()
        parentDir.mkdirs()

        val file = File(parentDir, "${Date().time}.jpg")
        if (file.exists()) {
            file.delete()
        }
        file.createNewFile()
        return file
    }


    /**
     * This function will get invoked when an image needs to compress.
     */
    open fun compressImage(file: File) {

    }


    open suspend fun compressAndStoreImage(file: File): File {
        return withContext(Dispatchers.IO) {
            val bitmap = file.toBitmap(720f, 720f) ?: throw Exception("")

            val compressedBitmap =
                compressBitmap(bitmap = bitmap, maxHeight = 720f, maxWidth = 720f) ?: bitmap

            val savedFile = storeImage(image = compressedBitmap, pictureFile = createImageFile())
                ?: throw Exception("could not save file")

            savedFile
        }
    }

    open fun checkRequiredPermissions() {

        val allPermissionGranted = requiredPermissions.values.map {
            ContextCompat.checkSelfPermission(
                requireContext(),
                it
            ) == PackageManager.PERMISSION_GRANTED
        }.all { it }

        if (allPermissionGranted) {
            openImageChoiceDialog()
            return
        }

        if (ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.CAMERA
            ) || ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) || ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) || ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            //show permission rational
            showDialog(
                title = "Permission required !",
                message = "Allow required permissions to complete the action.",
                showNegativeBtn = true,
                cancellable = false,
                negativeBtn = "Cancel",
                positiveBtn = "Allow",
            ) { d, i ->
                d.dismiss()
                if (i == POSITIVE_BTN) {
                    //ask permission
                    requestPermissions()
                } else {
                    //
                    handlePermissionRationalCancel()
                }
            }.show()
            return
        }

        //check if required permissions already asked


        val askedPermissionsAlready = requiredPermissions.entries.map {
            permissionPreferences.getBoolean(it.key, false)
        }.all { it }

        if (askedPermissionsAlready) {
            //show a dialog mentioning that all permissions asked already and to allow from settings
            showDialog(
                title = "Attention Required !",
                message = "It seems you have been asked to allow permissions before and you decided not to allow. Please allow Camera, Storage and Location permissions from App settings.",
                cancellable = false,
                showNegativeBtn = true,
                negativeBtn = "Cancel",
                positiveBtn = "App Settings",
            ) { d, i ->
                d.dismiss()
                if (i == POSITIVE_BTN) {
                    //open app settings
                    appSettingsLauncher.launch(Intent().apply {
                        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                    })
                } else {
                    handlePermissionReAskCancel()
                }
            }.show()
            return
        }

        requestPermissions()


    }

    open fun handlePermissionRationalCancel() {
        findNavController().popBackStack()
    }

    open fun handlePermissionReAskCancel() {
        findNavController().popBackStack()
    }


    private fun requestPermissions() {
        //save preference that permissions been asked
        requiredPermissions.entries.forEach {
            permissionPreferences.edit {
                putBoolean(it.key, true)
            }
        }
        requestPermissionsLauncher.launch(requiredPermissions.values.toTypedArray())
    }

}