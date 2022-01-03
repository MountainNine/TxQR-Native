package com.mtnine.txqrnative.vm

import android.util.Log
import com.mtnine.txqrnative.base.BaseViewModel
import com.mtnine.txqrnative.base.MutableSingleLiveData
import com.mtnine.txqrnative.util.EncodeUtil
import com.mtnine.txqrnative.util.QRGenerator.Companion.LOG_TAG
import java.io.*

class MainViewModel : BaseViewModel() {
    var onMakeQRClick = MutableSingleLiveData<Unit>()
    var onGoScanQRClick = MutableSingleLiveData<Unit>()

    fun clickMakeQR() {
        onMakeQRClick.setValue(Unit)
    }

    fun clickGoScanQR() {
        onGoScanQRClick.setValue(Unit)
    }

    fun splitAndEncode(input: InputStream) : List<ByteArray> {
        Log.d(LOG_TAG, "started making codes")
        val msgToEncode = readTextFromFile(input)
        val blocks = encode(msgToEncode.toByteArray(), 256, 5)
        Log.d(LOG_TAG, "data encoded")
        return blocks
    }

    private fun readTextFromFile(input: InputStream): String {
        val size = input.available()
        val buffer = ByteArray(size)
        input.read(buffer)
        input.close()
        val text = String(buffer)
        return text
    }

    private fun encode(file: ByteArray, blockSize: Int, extra: Int): List<ByteArray> {
        return EncodeUtil.encode(file, blockSize, extra)
    }
}