package com.mtnine.txqrnative.ui

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.mtnine.txqrnative.R
import com.mtnine.txqrnative.base.BaseActivity
import com.mtnine.txqrnative.databinding.ActivityMainBinding
import com.mtnine.txqrnative.util.FileUtil
import com.mtnine.txqrnative.util.FileUtil.generateGif
import com.mtnine.txqrnative.util.PermissionUtil
import com.mtnine.txqrnative.util.QRGenerator
import com.mtnine.txqrnative.util.QRGenerator.Companion.LOG_TAG
import com.mtnine.txqrnative.vm.MainViewModel
import java.nio.charset.Charset

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>(R.layout.activity_main) {
    override val viewModel: MainViewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.onMakeQRClick.observe(this, {
            if (!PermissionUtil.requestStorageAccessIfNecessary(this)) {
                val blocks = viewModel.splitAndEncode(assets.open("sample.txt"))
                val bitmaps = mutableListOf<Bitmap>()
                blocks.forEach { block ->
                    Log.d(LOG_TAG, "encoding images")
                    QRGenerator(
                        String(block, Charset.forName("UTF-8")), this
                    ).textToImageEncode(String(block, Charset.forName("UTF-8")))?.let {
                        bitmaps.add(it)
                    }
                }
                Log.d(LOG_TAG, "saving")
                FileUtil.saveGif(generateGif(bitmaps), this)
            }
        })

        viewModel.onGoScanQRClick.observe(this, {
            val intent = Intent(this, ScanActivity::class.java)
            startActivity(intent)
        })
    }


}