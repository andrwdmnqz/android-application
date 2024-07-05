package com.project.myproject.ui.fragments.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context

object NotificationUtils {
    fun createNotificationChannel(
        context: Context,
        channelId: String,
        channelName: String,
        channelDesc: String
    ) {
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelId, channelName, importance).apply {
            description = channelDesc
        }

        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}