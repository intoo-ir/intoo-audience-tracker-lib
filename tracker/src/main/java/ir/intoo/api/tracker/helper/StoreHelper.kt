package ir.intoo.api.tracker.helper

import android.content.Context
import android.content.SharedPreferences
import android.provider.Settings
import ir.intoo.api.tracker.R
import ir.intoo.api.tracker.model.Configure
import ir.intoo.api.tracker.model.Profile


class StoreHelper(var context: Context) {
    private val timeIntervalKey: String = "RunTimeIntervalSeconds"
    private val changeLocationDetectionMeters: String = "ChangeLocationDetectionMeters"
    private val accessTokenKey: String = "AccessToken"
    private val userAgeKey: String = "UserAge"
    private val userGenderKey: String = "UserGender"
    private val deviceIdKey: String = "DeviceId"

    private var sharedPref: SharedPreferences =
        context.getSharedPreferences(context.packageName + "_preferences", Context.MODE_PRIVATE)

    fun saveConfigure(configure: Configure) {
        with(sharedPref.edit()) {
            putLong(timeIntervalKey, configure.runTimeIntervalSeconds)
            putString(accessTokenKey, configure.accessToken)
            putFloat(changeLocationDetectionMeters, configure.changeLocationDetectionMeters)
            apply()
        }
    }

    fun getConfigure(): Configure {
        val configure = Configure()
        val defaultValue =
            context.resources.getInteger(R.integer.run_time_interval_seconds_default_key)
        configure.runTimeIntervalSeconds =
            sharedPref.getLong(timeIntervalKey, defaultValue.toLong())*1000
        configure.changeLocationDetectionMeters =
            sharedPref.getFloat(changeLocationDetectionMeters, 1.0F)
        configure.accessToken = sharedPref.getString(accessTokenKey, "").toString()
        return configure
    }


    fun saveProfile(profile: Profile) {
        with(sharedPref.edit()) {
            putInt(userAgeKey, profile.userAge)
            putInt(userGenderKey, profile.userGender)
            putString(deviceIdKey, profile.deviceId)
            apply()
        }
    }

    fun getProfile(): Profile {
        getImei(context)
        val profile = Profile()
        profile.userAge =
            sharedPref.getInt(userAgeKey, 0)
        profile.userGender =
            sharedPref.getInt(userGenderKey, 0)
        profile.deviceId = getImei(context)
        return profile
    }

    private fun getImei(context: Context): String? {
        return Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        )
    }

}