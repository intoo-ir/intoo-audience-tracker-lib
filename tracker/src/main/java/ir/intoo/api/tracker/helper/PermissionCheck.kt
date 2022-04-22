package ir.intoo.api.tracker

import android.Manifest
import android.Manifest.permission
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext


class PermissionCheck(private val context: Context) {
    var LOCATION_PERMISSIONS = arrayOf(
        permission.ACCESS_FINE_LOCATION,
        permission.ACCESS_COARSE_LOCATION
    )

    fun checkPermission(): Boolean {
        var isIDExists = true    // Executed first
        GlobalScope.launch {      // Executed second

            Dexter.withContext(context)
                .withPermissions(
                    permission.ACCESS_FINE_LOCATION,
                    permission.ACCESS_COARSE_LOCATION
                ).withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                        if (report.areAllPermissionsGranted()) {
                            isIDExists = true
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permissions: List<PermissionRequest?>?,
                        token: PermissionToken?
                    ) {
                        isIDExists = false
                    }
                }).check()

        }
        return isIDExists        // Executed third
    }

//    fun checkPermission(): Boolean {
//        var isGranted = checkSelfPermission(LOCATION_PERMISSIONS)
//        if (!isGranted) {
//
//            Dexter.withContext(context)
//                .withPermissions(
//                    permission.ACCESS_FINE_LOCATION,
//                    permission.ACCESS_COARSE_LOCATION
//                ).withListener(object : MultiplePermissionsListener {
//                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {
//                        if (report.areAllPermissionsGranted()) {
//
//                        }
//                    }
//
//                    override fun onPermissionRationaleShouldBeShown(
//                        permissions: List<PermissionRequest?>?,
//                        token: PermissionToken?
//                    ) {
//
//                    }
//                }).check()
//            if (RequestPermissionRationale(permissions)) {
//                ActivityCompat.requestPermissions(
//                    context as Activity,
//                    permissions,
//                    1
//                )
//            } else {
//                ActivityCompat.requestPermissions(
//                    context as Activity,
//                    permissions,
//                    1
//                )
//            }
//            isGranted = checkSelfPermission(LOCATION_PERMISSIONS)
//        }
//
//
//
//        return isGranted
//    }

    private fun RequestPermissionRationale(permissions: Array<String>): Boolean {
        var permissionCheck = true
        for (permission in permissions) {
            permissionCheck =
                permissionCheck and ActivityCompat.shouldShowRequestPermissionRationale(
                    context as Activity,
                    permission
                )
        }
        return permissionCheck
    }

    private fun checkSelfPermission(permissions: Array<String>): Boolean {
        var permissionCheck = true
        for (permission in permissions) {
            permissionCheck =
                permissionCheck and (ContextCompat.checkSelfPermission(context, permission)
                        == PackageManager.PERMISSION_GRANTED)
        }
        return permissionCheck
    }

    companion object {
        var STORAGE_AND_CAMERA_PERMISSIONS = arrayOf(
            permission.WRITE_EXTERNAL_STORAGE,
            permission.READ_EXTERNAL_STORAGE, permission.CAMERA
        )
        var STORAGE_PERMISSIONS = arrayOf(
            permission.WRITE_EXTERNAL_STORAGE,
            permission.READ_EXTERNAL_STORAGE
        )
        var CAMERA_PERMISSIONS =
            arrayOf(permission.CAMERA)
    }

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            LOCATION_PERMISSIONS = arrayOf(
                permission.ACCESS_FINE_LOCATION,
                permission.ACCESS_BACKGROUND_LOCATION,
                permission.ACCESS_COARSE_LOCATION
            )
        }
    }
}
