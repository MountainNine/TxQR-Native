package com.mtnine.txqrnative.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.mtnine.txqrnative.ui.ScanActivity.Companion.REQUEST_TAKE_PHOTO_CAMERA_PERMISSION

object PermissionUtil {
    fun requestStorageAccessIfNecessary(context: Context): Boolean {
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
                context as Activity, array,
                QRGenerator.REQUEST_STORAGE_PERMISSION
            )
            return true
        }
        return false
    }

    fun requestCameraAccessIfNecessary(context: Context): Boolean {
        val array = arrayOf(Manifest.permission.CAMERA)
        if (ContextCompat
                .checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                context as Activity, array,
                REQUEST_TAKE_PHOTO_CAMERA_PERMISSION)
            return true
        }
        return false
    }

    fun verifyPermissions(grantResults: IntArray): Boolean {
        // At least one result must be checked.
        if (grantResults.isEmpty()) {
            return false
        }

        // Verify that each required permission has been granted, otherwise return false.
        for (result in grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }
}