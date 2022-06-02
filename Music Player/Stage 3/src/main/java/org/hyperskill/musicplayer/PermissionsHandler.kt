package org.hyperskill.musicplayer

import android.Manifest
import android.app.Activity
import androidx.core.app.ActivityCompat

const val requestCode = 1

fun askPermission(activity: Activity) {
    val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    ActivityCompat.requestPermissions(activity, permissions, requestCode)
}