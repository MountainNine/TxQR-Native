package com.mtnine.txqrnative.vm

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.os.Environment
import android.util.Log
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.mtnine.txqrnative.base.BaseViewModel
import com.mtnine.txqrnative.base.MutableSingleLiveData
import com.mtnine.txqrnative.util.EncodeUtil
import java.io.*
import java.nio.charset.Charset
import java.util.*

class MainViewModel : BaseViewModel() {
    var onMakeQRClick = MutableSingleLiveData<Unit>()

    fun clickMakeQR() {
        onMakeQRClick.setValue(Unit)
    }

    fun splitAndEncode(input: InputStream) : List<ByteArray> {
        Log.d("TAG", "started making codes")
        val msgToEncode = readTextFromFile(input)
        val blocks = encode(msgToEncode.toByteArray(), 256, 5)
        Log.d("TAG", "data encoded")
        return blocks
    }

    fun readTextFromFile(input: InputStream): String {
        val size = input.available()
        val buffer = ByteArray(size)
        input.read(buffer)
        input.close()
        val text = String(buffer)
        return text
    }

    fun encode(file: ByteArray, blockSize: Int, extra: Int): List<ByteArray> {
        return EncodeUtil.encoder(file, blockSize, extra)
    }
}