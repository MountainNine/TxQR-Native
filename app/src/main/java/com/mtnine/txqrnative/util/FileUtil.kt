package com.mtnine.txqrnative.util

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.os.Environment
import android.util.Log
import com.mtnine.txqrnative.util.QRGenerator.Companion.LOG_TAG
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

object FileUtil {
    const val IMAGE_DIRECTORY = "/QRCodeDocuments"

    fun generateGif(bitmaps: List<Bitmap>): ByteArray {
        val byteOutStream = ByteArrayOutputStream()
        val gifEncoder = AnimatedGifEncoder()
        gifEncoder.start(byteOutStream)
        gifEncoder.setRepeat(0)
        gifEncoder.setFrameRate(3f)
        bitmaps.forEach { bitmap ->
            gifEncoder.addFrame(bitmap)
        }
        gifEncoder.finish()
        return byteOutStream.toByteArray()
    }

    fun saveGif(gifBytes: ByteArray, context: Context): String {
        val wallpaperDirectory = File(
            Environment
                .getExternalStorageDirectory()
                .toString() + IMAGE_DIRECTORY
        )
        // have the object build the directory structure, if needed.

        if (!wallpaperDirectory.exists()) {
            Log.d(LOG_TAG, "" + wallpaperDirectory.mkdirs())
            wallpaperDirectory.mkdirs()
        }

        try {
            val file = File(wallpaperDirectory, Calendar.getInstance()
                .timeInMillis.toString() + ".gif")
            file.createNewFile() //give read write permission
            val fileOutputStream = FileOutputStream(file)
            fileOutputStream.write(gifBytes)
            MediaScannerConnection.scanFile(context,
                arrayOf(file.path),
                arrayOf("image/gif"), null)
            fileOutputStream.close()
            Log.d(LOG_TAG, "File Saved :: ->>>>" + file.absolutePath)

            deleteTemp()
            return file.absolutePath
        } catch (e1: IOException) {
            Log.e("ERROR", "ioException while saving")
        }
        return ""
    }

    fun deleteTemp() {
        val dir = File(Environment.getExternalStorageDirectory()
            .toString() + IMAGE_DIRECTORY + "/Temp")
        if(dir.isDirectory) {
            val children = dir.list()
            children?.forEach { child ->
                File(dir, child).delete()
            }
            dir.delete()
        }

    }

    fun saveImage(bitmap: Bitmap?, context: Context): String {
        val bytes = ByteArrayOutputStream()
        bitmap!!.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
        val wallpaperDirectory = File(
            Environment
                .getExternalStorageDirectory()
                .toString() + IMAGE_DIRECTORY + "/Temp"
        )
        // have the object build the directory structure, if needed.

        if (!wallpaperDirectory.exists()) {
            Log.d(LOG_TAG, "" + wallpaperDirectory.mkdirs())
            wallpaperDirectory.mkdirs()
        }

        try {
            val file = File(
                wallpaperDirectory, Calendar.getInstance()
                    .timeInMillis.toString() + ".jpg"
            )
            file.createNewFile() //give read write permission
            val fileOutputStream = FileOutputStream(file)
            fileOutputStream.write(bytes.toByteArray())
            MediaScannerConnection.scanFile(
                context,
                arrayOf(file.path),
                arrayOf("image/jpeg"), null
            )
            fileOutputStream.close()
            Log.d(LOG_TAG, "File Saved :: ->>>>" + file.absolutePath)

            return file.absolutePath
        } catch (e1: IOException) {
            Log.d(LOG_TAG, "ioexception while saving")
        }
        return ""
    }

}