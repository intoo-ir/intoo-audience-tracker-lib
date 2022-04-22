package ir.intoo.api.tracker.helper

import android.content.Context
import android.util.Log
import ir.intoo.api.tracker.model.TrackerModel

class LocationReceiver(context: Context?, locationTracker: TrackerModel) {
    init {
        val storeHelper = StoreHelper(context!!)
        val profile = storeHelper.getProfile()
        locationTracker.userAge = profile.userAge
        locationTracker.userGender = profile.userGender
        locationTracker.deviceId = profile.deviceId
        Log.i(
            "LOG",
            "sss" + locationTracker.latitude + " " + locationTracker.longitude
        )
        val callApi = CallApi()
        callApi.sendLocation(context, locationTracker)
    }
}