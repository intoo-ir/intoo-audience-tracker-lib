package ir.intoo.api.tracker.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import ir.intoo.api.tracker.*
import ir.intoo.api.tracker.helper.LocationReceiver
import ir.intoo.api.tracker.helper.StoreHelper
import ir.intoo.api.tracker.model.TrackerModel


class TrackingService : Service() {
    private val channelId = "ForegroundService"
    private val tag = TrackingService::class.java.name
    private var locationTracker: LocationTracker? = null

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
//        val pStopSelf =
//            stopTrackingService(this).let {
//                PendingIntent.getService(
//                    this, 0,
//                    it, PendingIntent.FLAG_CANCEL_CURRENT
//                )
//            }
        createNotificationChannel()
        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Tracker Service")
//            .addAction(android.R.drawable.btn_dialog, "STOP", pStopSelf)
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
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
                        LocationReceiver(context, model, showLog)
                    }
                }
            }

            override fun onFailure(errorMessage: ErrorMessage) {
                Log.e(tag, errorMessage.name)
            }

            override fun onSuccess(successMessage: SuccessMessage) {
                Log.e(tag, successMessage.name)
            }
        }
        val storeHelper = StoreHelper(context = applicationContext)
        locationTracker?.apply {
            startLocationTracker(
                storeHelper.getConfigure().runTimeIntervalSeconds,
                storeHelper.getConfigure().runTimeIntervalSeconds,
                storeHelper.getConfigure().changeLocationDetectionMeters,
                LocationUpdate.ALL
            )
        }

    }

    companion object {
        private var showLog: Boolean = false
        private var isRunning = false

        fun startTrackingService(context: Context, showLog: Boolean) {
            val intent = Intent(context, TrackingService::class.java).apply {
                action = ServiceAction.ACTION_START.name
            }
            context.startService(intent)
            this.showLog = showLog
        }

        fun stopTrackingService(context: Context): Intent {
            val intent = Intent(context, TrackingService::class.java).apply {
                action = ServiceAction.ACTION_STOP.name
            }
            return intent
        }

        fun isRunningService(): Boolean {
            return isRunning
        }

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        isRunning = true
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

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                channelId, "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT,
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager!!.createNotificationChannel(serviceChannel)
        }
    }
}
