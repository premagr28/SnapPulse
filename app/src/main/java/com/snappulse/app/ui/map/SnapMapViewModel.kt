package com.snappulse.app.ui.map

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snappulse.app.BuildConfig
import com.snappulse.app.SnapPulseApp
import com.snappulse.app.data.model.BitmojiMood
import com.snappulse.app.data.model.FriendLocation
import com.snappulse.app.data.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SnapMapViewModel : ViewModel() {

    private val locationRepository = SnapPulseApp.instance.locationRepository

    val userProfile: StateFlow<UserProfile> = locationRepository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserProfile())

    val userLocation: StateFlow<Location?> = locationRepository.currentLocation
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val friends: StateFlow<List<FriendLocation>> = locationRepository.friends
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedFriend: StateFlow<FriendLocation?> = locationRepository.selectedFriend
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _showGhostModeSheet = MutableStateFlow(false)
    val showGhostModeSheet: StateFlow<Boolean> = _showGhostModeSheet.asStateFlow()

    val isApiKeyConfigured: Boolean
        get() {
            val key = BuildConfig.MAPS_API_KEY
            return key.isNotBlank() && key != "YOUR_GOOGLE_MAPS_API_KEY" && key != "DEFAULT_FALLBACK_KEY"
        }

    init {
        locationRepository.startLocationUpdates()
    }

    fun selectFriend(friend: FriendLocation?) {
        locationRepository.selectFriend(friend)
    }

    fun toggleGhostMode(enabled: Boolean, duration: String = "Until turned off") {
        locationRepository.setGhostMode(enabled, duration)
    }

    fun setShowGhostModeSheet(show: Boolean) {
        _showGhostModeSheet.value = show
    }

    fun updateUserStatus(status: String, mood: BitmojiMood) {
        locationRepository.updateUserStatus(status, mood)
    }
}
