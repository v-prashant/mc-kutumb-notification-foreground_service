package com.example.demo

import android.app.Service
import android.content.Intent
import android.os.CountDownTimer
import android.os.IBinder
import android.service.controls.actions.CommandAction
import androidx.core.app.NotificationManagerCompat

class NotificationService: Service() {

    private var countDownTimer: CountDownTimer? = null
    companion object {
        var action = "MY_ACTION"
        var timer = 30
        var EXTRA_TIME = "TIME"
    }

    override fun onBind(intent: Intent?): IBinder? {
       return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        countDownTimer?.cancel()
        val broadCast = Intent(action)
        startForeground(NotificationHelper.NOTIFICATION_ID, NotificationHelper.createNotificationBuilder(this@NotificationService, timer))
        countDownTimer = object : CountDownTimer((timer*1000).toLong(), 1000) {
            override fun onFinish() {
                stopSelf()
                timer = 30
            }
            override fun onTick(millisUntilFinished: Long) {
                NotificationHelper.showNotification(context = this@NotificationService, (millisUntilFinished/1000).toInt())
                broadCast.putExtra(EXTRA_TIME,  (millisUntilFinished/1000).toInt())
                sendBroadcast(broadCast)
            }
        }
        countDownTimer?.start()
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }
}