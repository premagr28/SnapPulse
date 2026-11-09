package com.snappulse.app.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.snappulse.app.data.model.BitmojiMood
import com.snappulse.app.data.model.FriendLocation
import com.snappulse.app.data.model.UserProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LocationRepository(private val context: Context) {

    private val fusedClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private val _currentLocation = MutableStateFlow<Location?>(null)
    val currentLocation: StateFlow<Location?> = _currentLocation.asStateFlow()

    private val _friends = MutableStateFlow<List<FriendLocation>>(emptyList())
    val friends: StateFlow<List<FriendLocation>> = _friends.asStateFlow()

    private val _selectedFriend = MutableStateFlow<FriendLocation?>(null)
    val selectedFriend: StateFlow<FriendLocation?> = _selectedFriend.asStateFlow()

    private var locationCallback: LocationCallback? = null

    init {
        // Initialize default friends
        initDefaultFriends(37.7749, -122.4194)
    }

    private fun initDefaultFriends(baseLat: Double, baseLng: Double) {
        val list = listOf(
            FriendLocation(
                id = "friend_1",
                name = "Sophia Chen",
                username = "sophia_c",
                avatarColorHex = 0xFFFF4081,
                bitmojiMood = BitmojiMood.COFFEE,
                latitude = baseLat + 0.0042,
                longitude = baseLng - 0.0031,
                statusText = "Sipping iced matcha 🍵",
                speedKmh = 0f,
                batteryPercent = 92,
                lastSeenTimeAgo = "Just now"
            ),
            FriendLocation(
                id = "friend_2",
                name = "Liam Walker",
                username = "liam_w",
                avatarColorHex = 0xFF00E676,
                bitmojiMood = BitmojiMood.CAR,
                latitude = baseLat - 0.0068,
                longitude = baseLng + 0.0055,
                statusText = "Cruising downtown 🚗",
                speedKmh = 48f,
                batteryPercent = 64,
                isCharging = true,
                lastSeenTimeAgo = "2m ago"
            ),
            FriendLocation(
                id = "friend_3",
                name = "Maya Patel",
                username = "mayap",
                avatarColorHex = 0xFF9C27B0,
                bitmojiMood = BitmojiMood.STUDY,
                latitude = baseLat + 0.0085,
                longitude = baseLng + 0.0072,
                statusText = "Building cool Android apps 💻",
                speedKmh = 0f,
                batteryPercent = 78,
                lastSeenTimeAgo = "5m ago"
            ),
            FriendLocation(
                id = "friend_4",
                name = "Ethan Davis",
                username = "ethan_d",
                avatarColorHex = 0xFFFF9100,
                bitmojiMood = BitmojiMood.MUSIC,
                latitude = baseLat - 0.0035,
                longitude = baseLng - 0.0082,
                statusText = "Vibing to new playlist 🎧",
                speedKmh = 5f,
                batteryPercent = 41,
                lastSeenTimeAgo = "12m ago"
            ),
            FriendLocation(
                id = "friend_5",
                name = "Chloe Miller",
                username = "chloe_m",
                avatarColorHex = 0xFF00B0FF,
                bitmojiMood = BitmojiMood.GYM,
                latitude = baseLat + 0.0019,
                longitude = baseLng + 0.0094,
                statusText = "Leg day 💪",
                speedKmh = 0f,
                batteryPercent = 88,
                lastSeenTimeAgo = "18m ago"
            )
        )
        _friends.value = list
    }

    @SuppressLint("MissingPermission")
    fun startLocationUpdates(onLocationObtained: ((Location) -> Unit)? = null) {
        try {
            fusedClient.lastLocation.addOnSuccessListener { loc: Location? ->
                if (loc != null) {
                    updateUserLocation(loc)
                    onLocationObtained?.invoke(loc)
                }
            }

            val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000L)
                .setMinUpdateIntervalMillis(3000L)
                .setMinUpdateDistanceMeters(2f)
                .build()

            locationCallback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    val loc = result.lastLocation ?: return
                    updateUserLocation(loc)
                    onLocationObtained?.invoke(loc)
                }
            }

            locationCallback?.let {
                fusedClient.requestLocationUpdates(request, it, Looper.getMainLooper())
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    fun stopLocationUpdates() {
        locationCallback?.let {
            fusedClient.removeLocationUpdates(it)
            locationCallback = null
        }
    }

    private fun updateUserLocation(loc: Location) {
        _currentLocation.value = loc
        val cur = _userProfile.value
        _userProfile.value = cur.copy(
            currentLat = loc.latitude,
            currentLng = loc.longitude
        )
        // Re-anchor friends around actual user location if first fix
        if (friends.value.isNotEmpty()) {
            val dist = Math.abs(friends.value.first().latitude - loc.latitude)
            if (dist > 1.0) {
                initDefaultFriends(loc.latitude, loc.longitude)
            }
        }
    }

    fun setGhostMode(enabled: Boolean, durationText: String = if (enabled) "Until turned off" else "Off") {
        _userProfile.value = _userProfile.value.copy(
            isGhostMode = enabled,
            ghostModeDurationText = durationText
        )
    }

    fun updateUserStatus(status: String, mood: BitmojiMood) {
        _userProfile.value = _userProfile.value.copy(
            currentStatus = status,
            bitmojiMood = mood
        )
    }

    fun selectFriend(friend: FriendLocation?) {
        _selectedFriend.value = friend
    }

    fun getFriendById(id: String): FriendLocation? {
        return _friends.value.firstOrNull { it.id == id }
    }
}
