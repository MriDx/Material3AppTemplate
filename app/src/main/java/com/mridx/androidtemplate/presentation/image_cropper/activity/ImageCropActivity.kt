package com.mridx.androidtemplate.presentation.image_cropper.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import com.canhub.cropper.CropImageView
import com.mridx.androidtemplate.R
import com.mridx.androidtemplate.databinding.CropActivityBinding
import com.mridx.androidtemplate.presentation.base.activity.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.util.*

@AndroidEntryPoint
class ImageCropActivity : BaseActivity(), CropImageView.OnSetImageUriCompleteListener,
    CropImageView.OnCropImageCompleteListener {


    open class CropImageActivityContract : ActivityResultContract<Uri, Uri>() {

        override fun createIntent(context: Context, input: Uri): Intent {
            val intent = Intent(context, ImageCropActivity::class.java)
            intent.putExtras(
                bundleOf(
                    "image_uri" to input.toString()
                )
            )
            return intent
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri {
            val imageUri = intent?.extras?.getString("image_uri") ?: ""
            return Uri.parse(imageUri)
        }

    }


    private lateinit var binding: CropActivityBinding

    private lateinit var imageUri: Uri


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = DataBindingUtil.setContentView<CropActivityBinding?>(this, R.layout.crop_activity)
            .apply { setLifecycleOwner { lifecycle } }


        val filePath = intent?.extras?.getString("image_uri")


        imageUri = Uri.parse(filePath)



        binding.cropImageView.setImageUriAsync(imageUri)


    }



    override fun onCropImageComplete(view: CropImageView, result: CropImageView.CropResult) {
        if (result.error != null) {
            setResult(Activity.RESULT_CANCELED)
        } else {
            val uri = result.getUriFilePath(this)
            val intent = Intent()
            intent.putExtras(Bundle().apply {
                putString("image_uri", uri)
            })
            setResult(Activity.RESULT_OK, intent)
        }
        finish()
    }

    override fun onSetImageUriComplete(view: CropImageView, uri: Uri, error: Exception?) {

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.crop_activity_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.saveBtn -> {
                //
                handleSave()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun handleSave() {
        //
        val outputFile = createOutputFile()
        binding.cropImageView.croppedImageAsync(
            customOutputUri = outputFile.toUri()
        )
    }


    private fun createOutputFile(): File {
        val parentDir = File(
            getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "cropped"
        )
        if (parentDir.exists()) {
            parentDir.deleteRecursively()
        }
        parentDir.mkdirs()

        val file = File(
            parentDir,
            "${Date().time}.jpg"
        )

        file.createNewFile()

        return file
    }

    public override fun onStart() {
        super.onStart()
        binding.cropImageView.setOnSetImageUriCompleteListener(this)
        binding.cropImageView.setOnCropImageCompleteListener(this)
    }

    public override fun onStop() {
        super.onStop()
        binding.cropImageView.setOnSetImageUriCompleteListener(null)
        binding.cropImageView.setOnCropImageCompleteListener(null)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("tmp_uri", imageUri.toString())
    }


}