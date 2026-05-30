package com.arievscalia.kerb.service

import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.car.app.connection.CarConnection
import com.arievscalia.kerb.data.datastore.KerbPreferences
import com.arievscalia.kerb.data.db.ReminderTrigger
import com.arievscalia.kerb.data.repository.TripRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CarConnectionService : Service() {

    @Inject lateinit var tripRepository: TripRepository
    @Inject lateinit var kerbPreferences: KerbPreferences

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var isConnected = false

    override fun onCreate() {
        super.onCreate()
        startForegroundCompat()
        observeCarConnection()
    }

    private fun startForegroundCompat() {
        val notification = NotificationHelper.buildServiceNotification(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                NotificationHelper.NOTIF_ID_SERVICE,
                notification,
                android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_CONNECTED_DEVICE
            )
        } else {
            startForeground(NotificationHelper.NOTIF_ID_SERVICE, notification)
        }
    }

    private fun observeCarConnection() {
        val carConnection = CarConnection(this)
        carConnection.type.observeForever { connectionType ->
            val connected = connectionType == CarConnection.CONNECTION_TYPE_PROJECTION
            if (connected && !isConnected) {
                isConnected = true
                onCarConnected()
            } else if (!connected && isConnected) {
                isConnected = false
                onCarDisconnected()
            }
        }
    }

    private fun onCarConnected() {
        serviceScope.launch {
            val trip = tripRepository.startTrip()
            kerbPreferences.setCurrentTripId(trip.id)
            notify(NotificationHelper.NOTIF_ID_CONNECT, NotificationHelper.buildConnectNotification(this@CarConnectionService))
        }
    }

    private fun onCarDisconnected() {
        serviceScope.launch {
            val tripId = kerbPreferences.currentTripId.first() ?: return@launch
            tripRepository.endTrip(tripId)

            val reminders = tripRepository.getRemindersForTripOnce(tripId)
            val hasPendingReminders = reminders.any {
                !it.isComplete && (it.trigger == ReminderTrigger.LEAVING || it.trigger == ReminderTrigger.BOTH)
            }
            if (hasPendingReminders) {
                notify(NotificationHelper.NOTIF_ID_DISCONNECT, NotificationHelper.buildDisconnectNotification(this@CarConnectionService))
            }

            kerbPreferences.setCurrentTripId(null)
        }
    }

    private fun notify(id: Int, notification: android.app.Notification) {
        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(id, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int =
        START_STICKY

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}
