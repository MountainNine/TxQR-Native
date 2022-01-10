package com.mtnine.txqrnative.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.google.zxing.Result
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.CaptureManager
import com.mtnine.txqrnative.R
import com.mtnine.txqrnative.base.BaseActivity
import com.mtnine.txqrnative.databinding.ActivityScanBinding
import com.mtnine.txqrnative.util.EncodeUtil
import com.mtnine.txqrnative.util.FileUtil
import com.mtnine.txqrnative.util.PermissionUtil
import com.mtnine.txqrnative.util.QRGenerator.Companion.LOG_TAG
import com.mtnine.txqrnative.vm.ScanViewModel

class ScanActivity : BaseActivity<ActivityScanBinding, ScanViewModel>(R.layout.activity_scan) {
    override val viewModel: ScanViewModel by lazy {
        ViewModelProvider(this).get(ScanViewModel::class.java)
    }

    lateinit var ltDecoder: EncodeUtil.LTDecoder
    lateinit var captureManager: CaptureManager
    var progress: Double = 0.0

    companion object {
        const val REQUEST_TAKE_PHOTO_CAMERA_PERMISSION = 100
        const val TOGGLE_FLASH = 200
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        captureManager = CaptureManager(this, binding.zxingScanner)
        ltDecoder = EncodeUtil.LTDecoder()
        captureManager.initializeFromIntent(intent, savedInstanceState)
        binding.zxingScanner.decodeContinuous(object: BarcodeCallback {
            override fun barcodeResult(result: BarcodeResult?) {
                val messageToBeam: String
                if (result != null) {
                    progress = try {
                        Log.d(LOG_TAG, "QR Data: ${result.text}")
                        ltDecoder.decodeBytes(result.text.toByteArray(Charsets.UTF_8)) * 100
                    } catch (e: Throwable) {
                        Log.e(LOG_TAG, "Exception decoding QR Code!", e)
                        progress
                    }
                }

                if (!ltDecoder.done) {
                    showToast("%.1f%% Done".format(progress))
                    Log.d(LOG_TAG, "QR is %.1f%% done decoding".format(progress))
                } else {
                    var beamedMessage: ByteArray? = null
                    try {
                        beamedMessage = ltDecoder.decodeDump()
                    } catch (e: Throwable) {
                        Log.e(LOG_TAG, "Exception decoding file!", e)
                    }
                    messageToBeam = String(beamedMessage!!)
                    Log.d(LOG_TAG, messageToBeam)

                    if (Build.VERSION.SDK_INT >= 29) {
                        FileUtil.saveTextNew(messageToBeam, applicationContext)
                    } else {
                        FileUtil.saveText(messageToBeam, applicationContext)
                    }
                    finish()
                }
            }

            override fun possibleResultPoints(resultPoints: MutableList<ResultPoint>?) {
            }

        })
    }

    override fun onResume() {
        super.onResume()
        captureManager.onResume()
    }

    override fun onPause() {
        super.onPause()
        captureManager.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        captureManager.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        captureManager.onSaveInstanceState(outState)
    }

    private fun finishWithError(errorCode: String) {
        val intent = Intent()
        intent.putExtra("ERROR_CODE", errorCode)
        finish()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_TAKE_PHOTO_CAMERA_PERMISSION -> {
                if (PermissionUtil.verifyPermissions(grantResults)) {
                    captureManager.onRequestPermissionsResult(requestCode,permissions, grantResults)
                } else {
                    finishWithError("PERMISSION_NOT_GRANTED")
                }
            }
            else -> {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }
}