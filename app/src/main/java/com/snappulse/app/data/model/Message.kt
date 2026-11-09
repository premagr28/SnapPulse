package com.snappulse.app.data.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class MessageType {
    TEXT,
    IMAGE,
    VIDEO,
    LOCATION_PIN
}

data class Message(
    val id: String,
    val conversationId: String,
    val senderId: String,
    val senderName: String,
    val content: String? = null,
    val type: MessageType = MessageType.TEXT,
    val mediaUri: String? = null,
    val mediaDurationSeconds: Int = 0,
    val locationLat: Double? = null,
    val locationLng: Double? = null,
    val locationName: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val isFromMe: Boolean = false,
    val isOpened: Boolean = false,
    val deliveredStatus: String = "Delivered"
) {
    val formattedTime: String
        get() {
            val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
            return sdf.format(Date(timestamp))
        }
}
