package ir.intoo.api.tracker.helper


import android.Manifest.permission
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener


class PermissionCheck(private val context: Context) {
    private var LOCATION_PERMISSIONS = arrayOf(
        permission.ACCESS_FINE_LOCATION,
        permission.ACCESS_COARSE_LOCATION
    )

    private fun getPermission() {
        Dexter.withContext(context)
            .withPermissions(
                permission.ACCESS_FINE_LOCATION,
                permission.ACCESS_COARSE_LOCATION
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {

                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest?>?,
                    token: PermissionToken?
                ) {

                }
            }).check()
    }

    fun checkSelfPermission(): Boolean {
        var permissionCheck = true
        for (permission in LOCATION_PERMISSIONS) {
            permissionCheck =
                permissionCheck and (ContextCompat.checkSelfPermission(context, permission)
                        == PackageManager.PERMISSION_GRANTED)
        }
        if (!permissionCheck) {
            getPermission()
        }
        return permissionCheck
    }

}
