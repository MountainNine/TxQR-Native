package com.mtnine.txqrnative

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.mtnine.txqrnative.base.BaseActivity
import com.mtnine.txqrnative.databinding.ActivityMainBinding
import com.mtnine.txqrnative.util.AnimatedGifEncoder
import com.mtnine.txqrnative.util.QRGenerator
import com.mtnine.txqrnative.util.QRGenerator.Companion.IMAGE_DIRECTORY
import com.mtnine.txqrnative.util.QRGenerator.Companion.REQUEST_STORAGE_PERMISSION
import com.mtnine.txqrnative.vm.MainViewModel
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.charset.Charset
import java.util.*

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>(R.layout.activity_main) {
    override val viewModel: MainViewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.onMakeQRClick.observe(this, {
            if (!requestStorageAccessIfNecessary(this)) {
                val blocks = viewModel.splitAndEncode(assets.open("sample.txt"))
                val bitmaps = mutableListOf<Bitmap>()
                blocks.forEach { block ->
                    Log.d("TAG", "encoding images")
                    QRGenerator(
                        String(block, Charset.forName("UTF-8")), this
                    ).textToImageEncode(String(block, Charset.forName("UTF-8")))?.let {
                        bitmaps.add(it)
                    }
                }
                Log.d("TAG", "saving")
                saveImage(generateGif(bitmaps), this)
            }
        })
    }


    fun saveImage(gifBytes: ByteArray, context: Context): String {
        val wallpaperDirectory = File(
            Environment
                .getExternalStorageDirectory()
                .toString() + IMAGE_DIRECTORY)
        // have the object build the directory structure, if needed.

        if (!wallpaperDirectory.exists()) {
            Log.d("TAG", "" + wallpaperDirectory.mkdirs())
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
            Log.d("TAG", "File Saved :: ->>>>" + file.absolutePath)

            return file.absolutePath
        } catch (e1: IOException) {
            Log.e("ERROR", "ioException while saving")
        }
        return ""
    }

    private fun generateGif(bitmaps: List<Bitmap>): ByteArray {
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

    private fun requestStorageAccessIfNecessary(context: Context): Boolean {
        val array = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, array,
                REQUEST_STORAGE_PERMISSION
            )
            return true
        }
        return false
    }
}