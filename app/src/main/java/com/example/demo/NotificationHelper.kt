package com.example.demo

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

object NotificationHelper {

    private val CHANNEL_ID = "123"
    private val CHANNEL_NAME = "Promotions"
    val NOTIFICATION_ID = 1

    fun createNotificationChannel(context: Context) {
        val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    fun createNotificationBuilder(context: Context, timer: Int): Notification {

        val intent = Intent(context, NotificationActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(context, 101, intent, PendingIntent.FLAG_IMMUTABLE)
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("Notification $timer")
            .setContentText("This is Notification")
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)
            .build()

        return notification

    }

    fun showNotification(context: Context, timer: Int) {
        NotificationManagerCompat.from(context)
            .notify(NOTIFICATION_ID, createNotificationBuilder(context = context, timer = timer))
    }
}