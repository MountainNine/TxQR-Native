package com.mtnine.txqrnative.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.Result
import com.mtnine.txqrnative.R
import com.mtnine.txqrnative.base.BaseActivity
import com.mtnine.txqrnative.util.EncodeUtil
import com.mtnine.txqrnative.util.FileUtil
import com.mtnine.txqrnative.util.PermissionUtil
import com.mtnine.txqrnative.util.PermissionUtil.requestCameraAccessIfNecessary
import com.mtnine.txqrnative.util.QRGenerator.Companion.LOG_TAG
import me.dm7.barcodescanner.zxing.ZXingScannerView

class ScanActivity : AppCompatActivity(), ZXingScannerView.ResultHandler {
    lateinit var scannerView: ZXingScannerView
    lateinit var ltDecoder: EncodeUtil.LTDecoder
    var progress: Double = 0.0

    companion object {
        const val REQUEST_TAKE_PHOTO_CAMERA_PERMISSION = 100
        const val TOGGLE_FLASH = 200
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = ""
        scannerView = ZXingScannerView(this)
        scannerView.setAutoFocus(true)
        scannerView.setAspectTolerance(0.5f)
        ltDecoder = EncodeUtil.LTDecoder()
        setContentView(scannerView)
    }

    override fun onResume() {
        super.onResume()
        scannerView.setResultHandler(this)
        // start camera immediately if permission is already given
        if (!requestCameraAccessIfNecessary(this)) {
            scannerView.startCamera()
        }
    }

    override fun onPause() {
        super.onPause()
        scannerView.stopCamera()
    }

    override fun handleResult(result: Result?) {
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
            Toast.makeText(this, "%.1f%% Done".format(progress), Toast.LENGTH_LONG).show()
            Log.d(LOG_TAG, "QR is %.1f%% done decoding".format(progress))
            scannerView.resumeCameraPreview(this)
        } else {
            var beamedMessage: ByteArray? = null
            try {
                beamedMessage = ltDecoder.decodeDump()
            } catch (e: Throwable) {
                Log.e(LOG_TAG, "Exception decoding file!", e)
            }
            messageToBeam = String(beamedMessage!!)
            Log.d(LOG_TAG, messageToBeam)

            FileUtil.saveText(messageToBeam, this)
            finish()
        }
    }

    private fun finishWithError(errorCode: String) {
        val intent = Intent()
        intent.putExtra("ERROR_CODE", errorCode)
        finish()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_TAKE_PHOTO_CAMERA_PERMISSION -> {
                if (PermissionUtil.verifyPermissions(grantResults)) {
                    scannerView.startCamera()
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