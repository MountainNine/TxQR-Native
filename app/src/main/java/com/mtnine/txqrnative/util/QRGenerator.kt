package com.mtnine.txqrnative.util

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.os.Environment
import android.util.Log
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.mtnine.txqrnative.vm.MainViewModel
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class QRGenerator(messageData: String, context: Context) {
    private lateinit var bitmap: Bitmap
    private lateinit var path: String

    init {
        bitmap = textToImageEncode(messageData)!!
        path = saveImage(bitmap, context)
    }

    fun getPath(): String {
        return path
    }

    private fun saveImage(bitmap: Bitmap?, context: Context): String {
        val bytes = ByteArrayOutputStream()
        bitmap!!.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
        val wallpaperDirectory = File(
            Environment
                .getExternalStorageDirectory()
                .toString() + IMAGE_DIRECTORY
        )
        // have the object build the directory structure, if needed.

        if (!wallpaperDirectory.exists()) {
            Log.d("TAG", "" + wallpaperDirectory.mkdirs())
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
            Log.d("TAG", "File Saved :: ->>>>" + file.absolutePath)

            return file.absolutePath
        } catch (e1: IOException) {
            Log.d("TAG", "ioexception while saving")
        }
        return ""
    }

    fun textToImageEncode(messageData: String): Bitmap? {
        val bitMatrix: BitMatrix
        try {
            bitMatrix = MultiFormatWriter().encode(
                messageData,
                BarcodeFormat.QR_CODE,
                QRCodeWidth, QRCodeWidth
            )
        } catch (e: IllegalArgumentException) {
            return null
        }

        val bitMatrixWidth = bitMatrix.width
        val bitMatrixHeight = bitMatrix.height
        val pixels = IntArray(bitMatrixWidth * bitMatrixHeight)

        for (y in 0 until bitMatrixHeight) {
            val offset = y * bitMatrixWidth
            for (x in 0 until bitMatrixWidth) {
                pixels[offset + x] = if (bitMatrix.get(x, y))
                    BLACK
                else
                    WHITE
            }
        }
        val bitmap = Bitmap.createBitmap(
            bitMatrixWidth,
            bitMatrixHeight,
            Bitmap.Config.RGB_565
        )
        bitmap.setPixels(
            pixels, 0, QRCodeWidth, 0, 0,
            bitMatrixWidth, bitMatrixHeight
        )
        return bitmap
    }

    
    companion object {
        const val BLACK: Int = 2130968606
        const val WHITE: Int = 2147483647
        const val REQUEST_STORAGE_PERMISSION = 101

        //Code Resolution
        const val QRCodeWidth = 968
        const val IMAGE_DIRECTORY = "/QRCodeDocuments"
    }
}