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
        path = ImageUtil.saveImage(bitmap, context)
    }

    fun getPath(): String {
        return path
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
    }
}