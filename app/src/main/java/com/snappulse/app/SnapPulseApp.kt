package com.snappulse.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.snappulse.app.data.repository.ChatRepository
import com.snappulse.app.data.repository.LocationRepository

class SnapPulseApp : Application() {

    lateinit var locationRepository: LocationRepository
        private set

    lateinit var chatRepository: ChatRepository
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this

        locationRepository = LocationRepository(this)
        chatRepository = ChatRepository(this)

        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val locationChannel = NotificationChannel(
                CHANNEL_LOCATION,
                "Snap Map Location Sharing",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows ongoing notification while your location is active on Snap Map"
            }

            val chatChannel = NotificationChannel(
                CHANNEL_CHAT,
                "Chats and Snaps",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for incoming chats, snaps, and friend updates"
            }

            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(locationChannel)
            manager.createNotificationChannel(chatChannel)
        }
    }

    companion object {
        const val CHANNEL_LOCATION = "channel_location_service"
        const val CHANNEL_CHAT = "channel_chat_messages"

        lateinit var instance: SnapPulseApp
            private set
    }
}
