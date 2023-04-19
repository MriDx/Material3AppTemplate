package com.mridx.androidtemplate.presentation.base.fragment.media

import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import com.mridx.androidtemplate.presentation.base.fragment.permission_launcher.PermissionLauncherFragment
import com.mridx.androidtemplate.presentation.image_cropper.activity.ImageCropActivity

abstract class MediaFragment<ViewBinding> : PermissionLauncherFragment<ViewBinding>() {


    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) {
            it?.let {
                imageCropFragmentLauncher.launch(it)
            }
        }


    open fun launchImagePicker() {
        imagePickerLauncher.launch("image/*")
    }


    private val imageCropFragmentLauncher =
        registerForActivityResult(ImageCropActivity.CropImageActivityContract()) {
            onCroppedResult(it)
        }

    open fun launchImageCropper(imageUri: Uri) {
        imageCropFragmentLauncher.launch(imageUri)
    }


    open fun onCroppedResult(fileUri: Uri?) {

    }


    private val cameraCaptureLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { captured ->
            onCameraCaptured(isCaptured = captured)
        }

    open fun onCameraCaptured(isCaptured: Boolean) {

    }

    open fun launchCamera(fileUri: Uri) {
        cameraCaptureLauncher.launch(fileUri)
    }



}