package org.hyperskill.musicplayer

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker

const val requestCode = 1

fun Activity.askPermission() {
    val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    ActivityCompat.requestPermissions(this, permissions, requestCode)
}

fun Activity.hasPermission(manifestPermission: String): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        this.checkSelfPermission(manifestPermission) == PackageManager.PERMISSION_GRANTED
    } else {
        PermissionChecker.checkSelfPermission(this, manifestPermission) == PermissionChecker.PERMISSION_GRANTED
    }
}