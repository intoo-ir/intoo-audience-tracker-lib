package ir.intoo.api.tracker


import android.app.Activity
import android.content.Context
import android.location.Location
import ir.intoo.api.tracker.helper.CallApi
import ir.intoo.api.tracker.helper.LocationReceiver
import ir.intoo.api.tracker.helper.StoreHelper
import ir.intoo.api.tracker.model.Profile
import ir.intoo.api.tracker.model.TrackerModel
import ir.intoo.api.tracker.service.TrackingService

class Tracker(var context: Activity, startService: Boolean = false) {

    companion object {
        var MALE = 1
        var FEMALE = 2
        fun saveProfile(context: Context, userAge: Int = 0, userGender: Int = MALE) {
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

        if (startService) {
            startService()
        } else {
            val locationTracker: LocationTracker = object : LocationTracker(context) {
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
                            LocationReceiver(context, model)
                        }
                    }
                }
            }
            locationTracker.startLocationTracker(LocationUpdate.ALL)

        }


    }

    private fun startService() {
        TrackingService.startTrackingService(context)
    }

    private fun stopService() {
        TrackingService.stopTrackingService(context)
    }

}