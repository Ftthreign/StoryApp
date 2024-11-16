package com.ftthreign.storyapp.helpers

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.exifinterface.media.ExifInterface
import com.ftthreign.storyapp.BuildConfig
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

private const val MAX_SIZE = 1000000
private const val FILE_FORMAT = "yyyyMMdd_HHmmss"
private val timeStamp : String = SimpleDateFormat(FILE_FORMAT, Locale.US).format(Date())

fun getImageUri(context: Context): Uri {
    var uri: Uri? = null
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "$timeStamp.jpg")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/MyCamera/")
        }
        uri = context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )
    }
    return uri ?: getImageUriForPreQ(context)
}

private fun getImageUriForPreQ(context: Context): Uri {
    val filesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val imageFile = File(filesDir, "/MyCamera/$timeStamp.jpg")
    if (imageFile.parentFile?.exists() == false) imageFile.parentFile?.mkdir()
    return FileProvider.getUriForFile(
        context,
        "${BuildConfig.APPLICATION_ID}.fileprovider",
        imageFile
    )
}

fun createCustomTempFile(context: Context): File {
    val filesDir = context.externalCacheDir
    return File.createTempFile(timeStamp, ".jpg", filesDir)
}

fun uriToFile(imageUri: Uri, context: Context): File {
    val myFile = createCustomTempFile(context)
    val inputStream = context.contentResolver.openInputStream(imageUri) as InputStream
    val outputStream = FileOutputStream(myFile)
    val buffer = ByteArray(1024)
    var length: Int
    while (inputStream.read(buffer).also { length = it } > 0) outputStream.write(buffer, 0, length)
    outputStream.close()
    inputStream.close()
    return myFile
}

fun File.reduceFileImage(): File {
    val file = this
    val bitmap = BitmapFactory.decodeFile(file.path).getRotatedBitmap(file)
    var compressQuality = 100
    var streamLength: Int
    do {
        val bmpStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
        val bmpPicByteArray = bmpStream.toByteArray()
        streamLength = bmpPicByteArray.size
        compressQuality -= 5
    } while (streamLength > MAX_SIZE)
    bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
    return file
}

fun Bitmap.getRotatedBitmap(file: File): Bitmap {
    val orientation = ExifInterface(file).getAttributeInt(
        ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED
    )
    return when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(this, 90F)
        ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(this, 180F)
        ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(this, 270F)
        ExifInterface.ORIENTATION_NORMAL -> this
        else -> this
    }
}

fun rotateImage(source: Bitmap, angle: Float): Bitmap {
    val matrix = Matrix()
    matrix.postRotate(angle)
    return Bitmap.createBitmap(
        source, 0, 0, source.width, source.height, matrix, true
    )
}

fun showMaterialDialog(
    context: Context,
    title: String,
    message: String,
    positiveButtonText: String,
    onPositiveClick: (() -> Unit)? = null,
) {
    MaterialAlertDialogBuilder(context)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton(positiveButtonText) { dialog, _ ->
            onPositiveClick?.invoke()
            dialog.dismiss()
        }
        .setCancelable(true)
        .show()
}

fun showToast(context: Context, message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(context, message, duration).show()
}

fun vectorToBitmap(@DrawableRes id: Int, @ColorInt color: Int, context : Context): BitmapDescriptor {
    val vectorDrawable = ResourcesCompat.getDrawable(context.resources, id, null)
    if (vectorDrawable == null) {
        Log.e("BitmapHelper", "Resource not found")
        return BitmapDescriptorFactory.defaultMarker()
    }
    val bitmap = Bitmap.createBitmap(
        100,
        100,
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
    DrawableCompat.setTint(vectorDrawable, color)
    vectorDrawable.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bitmap)
}

fun formatDate(date : String) : String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    inputFormat.timeZone = TimeZone.getTimeZone("UTC")
    val outputFormat = SimpleDateFormat("d MMMM yyyy HH:mm", Locale.getDefault())
    val parsedDate = inputFormat.parse(date)
    return outputFormat.format(parsedDate!!)
}