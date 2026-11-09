package com.snappulse.app.data.model

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

enum class BitmojiMood(val emoji: String, val title: String) {
    COFFEE("☕", "Coffee break"),
    CAR("🚗", "Driving"),
    MUSIC("🎧", "Vibing to tunes"),
    STUDY("💻", "Working on code"),
    GYM("🏋️", "Workout time"),
    CHILL("😎", "Chilling out"),
    FOOD("🍕", "Food hunt")
}

data class FriendLocation(
    val id: String,
    val name: String,
    val username: String,
    val avatarColorHex: Long = 0xFFFFFC00,
    val bitmojiMood: BitmojiMood = BitmojiMood.CHILL,
    val latitude: Double,
    val longitude: Double,
    val statusText: String,
    val speedKmh: Float = 0f,
    val batteryPercent: Int = 85,
    val isCharging: Boolean = false,
    val lastSeenTimeAgo: String = "Just now",
    val isOnline: Boolean = true
) {
    fun distanceTo(lat: Double, lng: Double): String {
        val r = 6371.0 // Earth radius in km
        val dLat = Math.toRadians(latitude - lat)
        val dLng = Math.toRadians(longitude - lng)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat)) * cos(Math.toRadians(latitude)) *
                sin(dLng / 2) * sin(dLng / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        val distanceKm = r * c
        return if (distanceKm < 1.0) {
            "${(distanceKm * 1000).toInt()} m away"
        } else {
            String.format("%.1f km away", distanceKm)
        }
    }
}
