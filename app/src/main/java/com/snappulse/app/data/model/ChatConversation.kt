package com.snappulse.app.data.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ChatConversation(
    val id: String,
    val friendId: String,
    val friendName: String,
    val friendUsername: String,
    val avatarColorHex: Long = 0xFFFFFC00,
    val bitmojiMood: BitmojiMood = BitmojiMood.CHILL,
    val lastMessagePreview: String,
    val lastMessageType: MessageType = MessageType.TEXT,
    val lastMessageTime: Long = System.currentTimeMillis(),
    val streakDays: Int = 0,
    val hasUnopenedSnap: Boolean = false,
    val isOnline: Boolean = true
) {
    val formattedTime: String
        get() {
            val now = System.currentTimeMillis()
            val diff = now - lastMessageTime
            return when {
                diff < 60_000 -> "Just now"
                diff < 3600_000 -> "${diff / 60_000}m"
                diff < 86400_000 -> "${diff / 3600_000}h"
                else -> {
                    val sdf = SimpleDateFormat("MMM d", Locale.getDefault())
                    sdf.format(Date(lastMessageTime))
                }
            }
        }
}
