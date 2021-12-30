package com.mtnine.txqrnative

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.mtnine.txqrnative.base.BaseActivity
import com.mtnine.txqrnative.databinding.ActivityMainBinding
import com.mtnine.txqrnative.util.QRGenerator
import com.mtnine.txqrnative.util.QRGenerator.Companion.REQUEST_STORAGE_PERMISSION
import com.mtnine.txqrnative.vm.MainViewModel
import java.nio.charset.Charset

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
                showToast("saved in QRCodeDocuments")
            }
        })
    }

    private fun requestStorageAccessIfNecessary(context: Context): Boolean {
        val array = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, array,
                REQUEST_STORAGE_PERMISSION)
            return true
        }
        return false
    }
}