package com.arievscalia.kerb.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.arievscalia.kerb.MainActivity

object NotificationHelper {

    const val CHANNEL_SERVICE = "kerb_service"
    const val CHANNEL_CONNECT = "kerb_connect"
    const val CHANNEL_DISCONNECT = "kerb_disconnect"

    const val NOTIF_ID_SERVICE = 1
    const val NOTIF_ID_CONNECT = 2
    const val NOTIF_ID_DISCONNECT = 3

    const val ACTION_ADD_REMINDER = "com.arievscalia.kerb.ACTION_ADD_REMINDER"
    const val ACTION_VIEW_LIST = "com.arievscalia.kerb.ACTION_VIEW_LIST"

    fun createChannels(context: Context) {
        val manager = context.getSystemService(NotificationManager::class.java)

        manager.createNotificationChannel(
            NotificationChannel(CHANNEL_SERVICE, "Kerb background service", NotificationManager.IMPORTANCE_LOW).apply {
                description = "Required while Kerb monitors car connection"
                setShowBadge(false)
            }
        )
        manager.createNotificationChannel(
            NotificationChannel(CHANNEL_CONNECT, "Car connected", NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = "Shown when you connect to Android Auto"
            }
        )
        manager.createNotificationChannel(
            NotificationChannel(CHANNEL_DISCONNECT, "Car disconnected", NotificationManager.IMPORTANCE_HIGH).apply {
                description = "Shown when you leave the car"
            }
        )
    }

    fun buildServiceNotification(context: Context): Notification =
        NotificationCompat.Builder(context, CHANNEL_SERVICE)
            .setContentTitle("Kerb is running")
            .setSmallIcon(android.R.drawable.ic_menu_directions)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setSilent(true)
            .build()

    fun buildConnectNotification(context: Context): Notification {
        val addReminderIntent = PendingIntent.getActivity(
            context, 0,
            Intent(context, MainActivity::class.java).apply {
                action = ACTION_ADD_REMINDER
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val dismissIntent = PendingIntent.getActivity(
            context, 1,
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(context, CHANNEL_CONNECT)
            .setContentTitle("You just entered the car")
            .setContentText("Need to set a reminder?")
            .setSmallIcon(android.R.drawable.ic_menu_directions)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .addAction(0, "Add reminder", addReminderIntent)
            .addAction(0, "Not now", dismissIntent)
            .build()
    }

    fun buildDisconnectNotification(context: Context): Notification {
        val viewListIntent = PendingIntent.getActivity(
            context, 2,
            Intent(context, MainActivity::class.java).apply {
                action = ACTION_VIEW_LIST
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val dismissIntent = PendingIntent.getActivity(
            context, 3,
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(context, CHANNEL_DISCONNECT)
            .setContentTitle("Time to leave — heads up")
            .setContentText("Double-check your list.")
            .setSmallIcon(android.R.drawable.ic_menu_directions)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .addAction(0, "View list", viewListIntent)
            .addAction(0, "Dismiss", dismissIntent)
            .build()
    }
}
