package com.mridx.androidtemplate.utils

import android.app.Activity
import android.content.*
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.JsonSyntaxException
import com.mridx.androidtemplate.BuildConfig
import com.mridx.androidtemplate.R
import com.mridx.androidtemplate.utils.APP_SETTINGS
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.net.UnknownHostException
import java.util.*
import java.util.concurrent.TimeUnit


fun Context.startActivity(activity: Class<*>, bundle: (() -> Bundle)? = null) {
    startActivity(Intent(this, activity).apply {
        bundle ?: return@apply
        putExtras(bundle.invoke())
    })
}


fun Fragment.startActivity(activity: Class<*>, bundle: (() -> Bundle)? = null) {
    requireContext().startActivity(activity = activity, bundle = bundle)
}


fun storeImage(image: Bitmap, pictureFile: File): File? {
    if (!pictureFile.exists()) {
        pictureFile.createNewFile()
    }
    try {
        val fos = FileOutputStream(pictureFile)
        image.compress(Bitmap.CompressFormat.PNG, 90, fos)
        fos.close()
        return pictureFile
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
        Log.d("mridx", "File not found: ${e.message}")
    } catch (e: IOException) {
        e.printStackTrace()
        Log.d("mridx", "Error accessing file: ${e.message}")
    }
    return null
}


const val POSITIVE_BTN = 1
const val NEGATIVE_BTN = 2
fun Context.showDialog(
    title: String,
    message: String,
    positiveBtn: String?,
    negativeBtn: String = "Cancel",
    showNegativeBtn: Boolean = false,
    cancellable: Boolean = false,
    onPressed: ((d: DialogInterface, i: Int) -> Unit)
) = run {
    AlertDialog.Builder(this).apply {
        setTitle(title)
        setMessage(message)
        if (positiveBtn != null)
            setPositiveButton(positiveBtn) { d, _ -> onPressed.invoke(d, POSITIVE_BTN) }
        if (showNegativeBtn)
            setNegativeButton(negativeBtn) { d, _ -> onPressed.invoke(d, NEGATIVE_BTN) }
        setCancelable(cancellable)
    }.create()
}

fun Fragment.showDialog(
    title: String,
    message: String,
    positiveBtn: String?,
    negativeBtn: String = "Cancel",
    showNegativeBtn: Boolean = false,
    cancellable: Boolean = false,
    onPressed: ((d: DialogInterface, i: Int) -> Unit)
) = requireContext().showDialog(
    title,
    message,
    positiveBtn,
    negativeBtn,
    showNegativeBtn,
    cancellable,
    onPressed
)


fun Fragment.appSettings() {
    requireActivity().appSettings()
}

fun Activity.appSettings() {
    Intent().also { intent ->
        intent.action =
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        intent.data = Uri.fromParts(
            "package",
            BuildConfig.APPLICATION_ID,
            null
        )
        startActivityForResult(
            intent,
            APP_SETTINGS
        )
    }
}


fun parseException(e: Throwable?): String {
    return when (e) {
        is UnknownHostException -> {
            "No internet available"
        }
        is JsonSyntaxException -> {
            //"We are having some trouble connecting you to the server right now."
            "We are having some issue right now."
        }
        is Exception -> {
            when {
                e.message?.contains("Unable to resolve host", ignoreCase = true) ?: false -> {
                    "No internet available"
                }
                else -> e.message ?: "No internet available"
            }
        }
        else -> e?.message ?: "No internet available"
    }

}


fun View.hideKeyboard() {
    val imm: InputMethodManager =
        context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}


fun TextInputEditText.parentLayout(): TextInputLayout {
    return parent.parent as TextInputLayout
}


fun Context.makeCall(number: String) {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.data = Uri.parse("tel:$number")
    startActivity(intent)
}

fun Fragment.makeCall(number: String) {
    requireContext().makeCall(number)
}


fun scaleBitmap(image: Bitmap, width: Int, height: Int): Bitmap {
    return Bitmap.createScaledBitmap(image, width, height, true)
}


fun compressBitmap(bitmap: Bitmap, maxHeight: Float, maxWidth: Float): Bitmap? {

    var scaledBitmap: Bitmap?
    var bmp = bitmap.copy(bitmap.config, true)


    var actualHeight = /*options.outHeight*/ bmp.height
    var actualWidth = /*options.outWidth*/ bmp.width

    var imgRatio = actualWidth.toFloat() / actualHeight.toFloat()
    val maxRatio = maxWidth / maxHeight

    if (actualHeight > maxHeight || actualWidth > maxWidth) {
        when {
            imgRatio < maxRatio -> {
                imgRatio = maxHeight / actualHeight
                actualWidth = (imgRatio * actualWidth).toInt()
                actualHeight = maxHeight.toInt()
            }
            imgRatio > maxRatio -> {
                imgRatio = maxWidth / actualWidth
                actualHeight = (imgRatio * actualHeight).toInt()
                actualWidth = maxWidth.toInt()
            }
            else -> {
                actualHeight = maxHeight.toInt()
                actualWidth = maxWidth.toInt()
            }
        }
    }

    try {
        scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.RGB_565)
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }

    val ratioX = actualWidth / bmp.width.toFloat()
    val ratioY = actualHeight / bmp.height.toFloat()
    val middleX = actualWidth / 2.0f
    val middleY = actualHeight / 2.0f

    val scaleMatrix = Matrix()
    scaleMatrix.setScale(ratioX, ratioY, middleX, middleY)

    val canvas = Canvas(scaledBitmap)
    canvas.setMatrix(scaleMatrix)
    canvas.drawBitmap(
        bmp,
        middleX - bmp.width / 2,
        middleY - bmp.height / 2,
        Paint(Paint.FILTER_BITMAP_FLAG)
    )
    bmp.recycle()

    return scaledBitmap
}


fun Fragment.getClipboard(): ClipboardManager {
    return requireContext().getClipboard()
}

fun Context.getClipboard(): ClipboardManager {
    return getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
}

fun Context.copyToClipboard(
    dataToCopy: String,
    title: String
) {
    val obj = ClipData.newPlainText(title, dataToCopy)
    getClipboard().setPrimaryClip(obj)
    Toast.makeText(this, "$title copied to clipboard !", Toast.LENGTH_SHORT).show()
}

fun Fragment.copyToClipboard(dataToCopy: String, title: String) {
    requireContext().copyToClipboard(dataToCopy, title)
}

fun Context.openInBrower(url: String) {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.data = Uri.parse(url)
    startActivity(intent)
}


fun TextView.disableCopyPaste() {
    isLongClickable = false
    setTextIsSelectable(false)
    customSelectionActionModeCallback = object : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode?, menu: Menu) = false
        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu) = false
        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem) = false
        override fun onDestroyActionMode(mode: ActionMode?) {}
    }
    //disable action mode when edittext gain focus at first
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        customInsertionActionModeCallback = object : ActionMode.Callback {
            override fun onCreateActionMode(mode: ActionMode?, menu: Menu) = false
            override fun onPrepareActionMode(mode: ActionMode?, menu: Menu) = false
            override fun onActionItemClicked(mode: ActionMode?, item: MenuItem) = false
            override fun onDestroyActionMode(mode: ActionMode?) {}
        }
    }
}


fun Long.hourDifferenceFromLastFetch() =
    TimeUnit.MILLISECONDS.toHours(Date().time - this)


fun String.replaceNewLine(): String {
    return this.replace("\n\n", "<p>").replace("\n", "<br>")
}


fun successSnackbar(
    view: View,
    message: String,
    duration: Int = Snackbar.LENGTH_LONG,
    action: String? = null,
    onActionClicked: (() -> Unit)? = null
) {
    Snackbar.make(view, message, duration).apply {
        animationMode = Snackbar.ANIMATION_MODE_SLIDE
        if (action != null) {
            setAction(action) {
                onActionClicked?.invoke()
            }
            setActionTextColor(ContextCompat.getColor(view.context, R.color.green_800))
        }
        setBackgroundTint(ContextCompat.getColor(view.context, R.color.green_200))
        setTextColor(ContextCompat.getColor(view.context, R.color.green_800))
    }.show()
}


fun errorSnackbar(
    view: View,
    message: String,
    duration: Int = Snackbar.LENGTH_LONG,
    action: String? = null,
    onActionClicked: (() -> Unit)? = null
) {
    Snackbar.make(view, message, duration).apply {
        animationMode = Snackbar.ANIMATION_MODE_SLIDE
        if (action != null) {
            setAction(action) {
                onActionClicked?.invoke()
            }
            setActionTextColor(ContextCompat.getColor(view.context, R.color.red_800))
        }
        setBackgroundTint(ContextCompat.getColor(view.context, R.color.red_200))
        setTextColor(ContextCompat.getColor(view.context, R.color.red_800))
    }.show()
}

fun infoSnackbar(
    view: View,
    message: String,
    duration: Int = Snackbar.LENGTH_LONG,
    action: String? = null,
    onActionClicked: (() -> Unit)? = null
) {
    Snackbar.make(view, message, duration).apply {
        animationMode = Snackbar.ANIMATION_MODE_SLIDE
        if (action != null) {
            setAction(action) {
                onActionClicked?.invoke()
            }
            //setActionTextColor(ContextCompat.getColor(view.context, R.color.red_800))
        }
        //setBackgroundTint(ContextCompat.getColor(view.context, R.color.red_200))
        //setTextColor(ContextCompat.getColor(view.context, R.color.red_800))
    }.show()
}


fun Any?.isNull(): Boolean {
    return this == null
}


fun openMapAtLocation(latitude: String, longitude: String): Intent {
    val url = "https://www.google.com/maps/search/?api=1&query=${latitude},${longitude}"
    return Intent(Intent.ACTION_VIEW, Uri.parse(url))
}





