package com.example.workmanagere

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager

class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        val notificationChannel = NotificationChannel(
            "work_manager_001",
            "General Notifications",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val notificationMgr = getSystemService(NotificationManager::class.java)
        notificationMgr.createNotificationChannel(notificationChannel)
    }
}