package com.snappulse.app.data.model

data class UserProfile(
    val id: String = "user_me",
    val name: String = "Alex Rivera",
    val username: String = "alex_snap",
    val snapScore: Int = 48290,
    val isGhostMode: Boolean = false,
    val ghostModeDurationText: String = "Off",
    val currentLat: Double = 37.7749,
    val currentLng: Double = -122.4194,
    val currentStatus: String = "Exploring SnapMap 🗺️",
    val bitmojiMood: BitmojiMood = BitmojiMood.CHILL
)

