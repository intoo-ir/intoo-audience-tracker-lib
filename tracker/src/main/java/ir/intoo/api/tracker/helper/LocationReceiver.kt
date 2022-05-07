package ir.intoo.api.tracker.helper

import android.content.Context
import android.util.Log
import android.widget.Toast
import ir.intoo.api.tracker.Tracker
import ir.intoo.api.tracker.model.TrackerModel

class LocationReceiver(
    context: Context?,
    locationTracker: TrackerModel,
    showLog: Boolean = false
) {
    init {
        val storeHelper = StoreHelper(context!!)
        val profile = storeHelper.getProfile()
        locationTracker.userAge = if (profile.userAge == 0) null else profile.userAge
        locationTracker.userGender = profile.userGender
        locationTracker.deviceId = profile.deviceId
        val callApi = CallApi()
        callApi.sendLocation(context, locationTracker)
        if (showLog) {
            Toast.makeText(
                context,
                "lat: ${locationTracker.latitude} lon: ${locationTracker.longitude} Age: ${locationTracker.userAge} Gender:  ${locationTracker.userGender}",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}