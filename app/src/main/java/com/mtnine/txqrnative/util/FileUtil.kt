package com.mtnine.txqrnative.util

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import com.mtnine.txqrnative.util.QRGenerator.Companion.LOG_TAG
import java.io.*
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

    fun saveGifOld(gifBytes: ByteArray, context: Context): String {
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

            return file.absolutePath
        } catch (e1: IOException) {
            Log.e("ERROR", "ioException while saving")
        }
        return ""
    }

    fun saveGifNew(gifBytes: ByteArray, context: Context) {
        val contentValues = ContentValues()
        contentValues.put(
            MediaStore.Images.Media.DISPLAY_NAME, Calendar.getInstance()
                .timeInMillis.toString() + ".gif"
        )
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/*")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentValues.put(MediaStore.Images.Media.IS_PENDING, 1);
        }

        val contentResolver = context.contentResolver
        val item =
            contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        try {
            val pdf = item?.let { contentResolver.openFileDescriptor(it, "w", null) }
            if (pdf != null) {
                val fileOutputStream = FileOutputStream(pdf.fileDescriptor)
                fileOutputStream.write(gifBytes)
                fileOutputStream.close()

                contentValues.clear();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    contentValues.put(MediaStore.Images.Media.IS_PENDING, 0);
                }
                contentResolver.update(item, contentValues, null, null);
            }
        } catch (e1: IOException) {
            e1.printStackTrace()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
    }

    fun saveImage(byteStr: String, context: Context) {
        try {
            val encodedByte = byteStr.toByteArray()
            val dir = File(
                Environment
                    .getExternalStorageDirectory()
                    .toString() + IMAGE_DIRECTORY
            )
            if (!dir.exists()) {
                Log.d(LOG_TAG, "" + dir.mkdirs())
                dir.mkdirs()
            }

            val file = File(
                dir, Calendar.getInstance()
                    .timeInMillis.toString() + ".png"
            )
            file.createNewFile() //give read write permission
            val fileOutputStream = FileOutputStream(file)
            fileOutputStream.write(encodedByte)
            MediaScannerConnection.scanFile(
                context,
                arrayOf(file.path),
                arrayOf("image/png"), null
            )
            fileOutputStream.close()
            Log.d(LOG_TAG, "File Saved :: ->>>>" + file.absolutePath)
        } catch (e1: IOException) {
            Log.d(LOG_TAG, "ioexception while saving")
        }
    }

    fun saveText(text: String, context: Context) {
        val dir = File(
            Environment
                .getExternalStorageDirectory()
                .toString() + IMAGE_DIRECTORY
        )

        if (!dir.exists()) {
            Log.d(LOG_TAG, "" + dir.mkdirs())
            dir.mkdirs()
        }

        try {
            val file = File(dir, Calendar.getInstance().timeInMillis.toString() + ".txt")
            file.createNewFile()
            val fileOutputStream = FileOutputStream(file)
            fileOutputStream.write(text.toByteArray())
            MediaScannerConnection.scanFile(
                context,
                arrayOf(file.path),
                arrayOf("text/plain"), null
            )
            fileOutputStream.close()
            Log.d(LOG_TAG, "File Saved :: ->>>>" + file.absolutePath)
        } catch (e: IOException) {
            Log.e(LOG_TAG, "File write failed: $e")
        }
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    fun saveTextNew(text: String, context: Context) {
        val contentValues = ContentValues()
        contentValues.put(
            MediaStore.Downloads.DISPLAY_NAME, Calendar.getInstance()
                .timeInMillis.toString() + ".txt"
        )
        contentValues.put(MediaStore.Downloads.MIME_TYPE, "text/*")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentValues.put(MediaStore.Downloads.IS_PENDING, 1);
        }

        val contentResolver = context.contentResolver
        val item = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

        try {
            val pdf = item?.let { contentResolver.openFileDescriptor(it, "w", null) }
            if (pdf != null) {
                val fileOutputStream = FileOutputStream(pdf.fileDescriptor)
                fileOutputStream.write(text.toByteArray())
                fileOutputStream.close()

                contentValues.clear();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    contentValues.put(MediaStore.Images.Media.IS_PENDING, 0);
                }
                contentResolver.update(item, contentValues, null, null);
            }
        } catch (e1: IOException) {
            e1.printStackTrace()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
    }
}