package ir.intoo.api.tracker.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import ir.intoo.api.tracker.ErrorMessage
import ir.intoo.api.tracker.LocationTracker
import ir.intoo.api.tracker.LocationUpdate
import ir.intoo.api.tracker.SuccessMessage
import ir.intoo.api.tracker.helper.LocationReceiver
import ir.intoo.api.tracker.model.TrackerModel


class TrackingService : Service() {
    private val CHANNEL_ID = "ForegroundService"

    var locationTracker: LocationTracker? = null

    /**
     * Handle action Stop in the provided background thread with the provided
     * parameters.
     */
    private fun handleActionStop() {
        locationTracker?.stopLocationTracker(LocationUpdate.ALL)
        stopSelf()
    }

    /**
     * Handle action Start in the provided background thread with the provided
     * parameters.
     */
    private fun handleActionStart() {
        locationTracker?.apply {
            startLocationTracker(2000, 2000, 1f, LocationUpdate.ALL)
//            startLocationTracker(LocationUpdate.ALL)
//
//            Handler().postDelayed(
//                {
//                    TrackingService.locationTracker?.stopLocationTracker(LocationUpdate.ALL)
//                },5000
//            )

        }

    }

    companion object {
        @JvmStatic
        fun startTrackingService(context: Context) {
            val intent = Intent(context, TrackingService::class.java).apply {
                action = ServiceAction.ACTION_START.name
            }
            context.startService(intent)
        }

        @JvmStatic
        fun stopTrackingService(context: Context): Intent? {
            val intent = Intent(context, TrackingService::class.java).apply {
                action = ServiceAction.ACTION_STOP.name
            }
            return intent
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val input = intent?.getStringExtra("inputExtra")
        createNotificationChannel()
        val pStopSelf =
            stopTrackingService(this)?.let {
                PendingIntent.getService(
                    this, 0,
                    it, PendingIntent.FLAG_CANCEL_CURRENT
                )
            }

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Foreground Service Kotlin Example")
            .setContentText(input)
            .addAction(android.R.drawable.btn_dialog, "STOP", pStopSelf)
            .setSmallIcon(androidx.appcompat.R.drawable.abc_ab_share_pack_mtrl_alpha)
            .build()
        startForeground(1, notification)
        locationTracker = object : LocationTracker(this@TrackingService) {

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

            override fun onFailure(errorMessage: ErrorMessage) {
                Log.e("ERROR", errorMessage.name)
            }

            override fun onSuccess(successMessage: SuccessMessage) {
                Log.e("SUCCESS", successMessage.name)
            }
        }
        when (intent?.action) {
            ServiceAction.ACTION_START.name -> {
                handleActionStart()
            }
            ServiceAction.ACTION_STOP.name -> {
                handleActionStop()
            }
        }
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID, "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT,
            )

            val manager = getSystemService(NotificationManager::class.java)
            manager!!.createNotificationChannel(serviceChannel)
        }
    }

}
