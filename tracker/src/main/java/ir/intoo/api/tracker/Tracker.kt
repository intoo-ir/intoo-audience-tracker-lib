@file:JvmName("Tracker")

package ir.intoo.api.tracker


import android.Manifest
import android.app.Activity
import android.content.Context
import android.location.Location
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import ir.intoo.api.tracker.helper.CallApi
import ir.intoo.api.tracker.helper.LocationReceiver
import ir.intoo.api.tracker.helper.StoreHelper
import ir.intoo.api.tracker.model.Profile
import ir.intoo.api.tracker.model.TrackerModel
import ir.intoo.api.tracker.service.TrackingService

class Tracker(
    var context: Activity,
    var showLog: Boolean = false
) {
    private lateinit var locationTracker: LocationTracker
    private var isRunningTracker = false

    enum class Gender() {
        male, female
    }

    companion object {


        fun saveProfile(
            context: Context,
            userAge: Int?,
            userGender: String?
        ) {
            val profile = Profile()
            profile.userAge = userAge
            profile.userGender = userGender
            StoreHelper(context).saveProfile(profile)
        }
    }


    init {
        val storeHelper = StoreHelper(context)
        if (storeHelper.getConfigure().accessToken.isEmpty()) {
            val callApi = CallApi()
            callApi.getConfigure(context)
        }
    }

    fun start(startService: Boolean = false) {
        Dexter.withContext(context)
            .withPermissions(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (startService) {
                        startService()
                    } else {
                        isRunningTracker = true
                        locationTracker = object : LocationTracker(context) {
                            override fun onSuccess(successMessage: SuccessMessage) {}
                            override fun onFailure(errorMessage: ErrorMessage) {}
                            override fun onLocationChanged(
                                networkLocation: Location?,
                                networkLocationSpeed: Double,
                                locationUpdate: LocationUpdate
                            ) {
                                if (networkLocation != null) {
                                    run {
                                        val model = TrackerModel()
                                        model.longitude = networkLocation.longitude
                                        model.latitude = networkLocation.latitude
                                        model.speed = networkLocation.speed
                                        model.time = networkLocation.time
                                        model.altitude = networkLocation.altitude
                                        model.networkName = locationUpdate.name
                                        LocationReceiver(context, model, showLog)
                                    }
                                }
                            }
                        }
                        locationTracker.startLocationTracker(LocationUpdate.ALL)

                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest?>?,
                    token: PermissionToken?
                ) {

                }
            }).check()
    }

    fun stop() {
        locationTracker.stopLocationTracker(LocationUpdate.ALL)
        isRunningTracker = false
    }

    fun isRunningTracker(): Boolean {
        return isRunningTracker
    }

    private fun startService() {
        TrackingService.startTrackingService(context, showLog)
    }

    fun stopService() {
        val intent = TrackingService.stopTrackingService(context)
        context.stopService(intent)
    }

    fun isRunningService(): Boolean {
        return TrackingService.isRunningService()
    }

}