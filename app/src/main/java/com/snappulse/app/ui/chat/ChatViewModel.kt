package com.snappulse.app.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snappulse.app.SnapPulseApp
import com.snappulse.app.data.model.ChatConversation
import com.snappulse.app.data.model.Message
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class ChatViewModel : ViewModel() {

    private val chatRepo = SnapPulseApp.instance.chatRepository
    private val locationRepo = SnapPulseApp.instance.locationRepository

    val conversations: StateFlow<List<ChatConversation>> = chatRepo.conversations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentConvId = MutableStateFlow<String?>(null)
    val currentConvId: StateFlow<String?> = _currentConvId.asStateFlow()

    val currentMessages: StateFlow<List<Message>> = combine(
        chatRepo.messages,
        _currentConvId
    ) { messagesMap, convId ->
        if (convId != null) messagesMap[convId] ?: emptyList() else emptyList()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectConversation(convId: String) {
        _currentConvId.value = convId
    }

    fun sendText(convId: String, text: String) {
        chatRepo.sendTextMessage(convId, text)
    }

    fun sendImage(convId: String, uri: String) {
        chatRepo.sendImageMessage(convId, uri)
    }

    fun sendVideo(convId: String, uri: String, durationSec: Int = 10) {
        chatRepo.sendVideoMessage(convId, uri, durationSec)
    }

    fun sendCurrentLocation(convId: String) {
        val loc = locationRepo.currentLocation.value
        val lat = loc?.latitude ?: locationRepo.userProfile.value.currentLat
        val lng = loc?.longitude ?: locationRepo.userProfile.value.currentLng
        chatRepo.sendLocationMessage(convId, lat, lng, "📍 Live Location")
    }

    fun markOpened(convId: String, messageId: String) {
        chatRepo.markMessageAsOpened(convId, messageId)
    }

    fun getConversation(convId: String): ChatConversation? {
        return conversations.value.firstOrNull { it.id == convId }
    }
}
