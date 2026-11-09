package com.snappulse.app.data.repository

import android.content.Context
import com.snappulse.app.data.model.BitmojiMood
import com.snappulse.app.data.model.ChatConversation
import com.snappulse.app.data.model.Message
import com.snappulse.app.data.model.MessageType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class ChatRepository(private val context: Context) {

    private val scope = CoroutineScope(Dispatchers.Default)

    private val _conversations = MutableStateFlow<List<ChatConversation>>(emptyList())
    val conversations: StateFlow<List<ChatConversation>> = _conversations.asStateFlow()

    private val _messages = MutableStateFlow<Map<String, List<Message>>>(emptyMap())
    val messages: StateFlow<Map<String, List<Message>>> = _messages.asStateFlow()

    init {
        initDefaultChats()
    }

    private fun initDefaultChats() {
        val now = System.currentTimeMillis()

        val sampleConvs = listOf(
            ChatConversation(
                id = "conv_1",
                friendId = "friend_1",
                friendName = "Sophia Chen",
                friendUsername = "sophia_c",
                avatarColorHex = 0xFFFF4081,
                bitmojiMood = BitmojiMood.COFFEE,
                lastMessagePreview = "Delivered",
                lastMessageType = MessageType.IMAGE,
                lastMessageTime = now - 120_000,
                streakDays = 42,
                hasUnopenedSnap = true,
                isOnline = true
            ),
            ChatConversation(
                id = "conv_2",
                friendId = "friend_2",
                friendName = "Liam Walker",
                friendUsername = "liam_w",
                avatarColorHex = 0xFF00E676,
                bitmojiMood = BitmojiMood.CAR,
                lastMessagePreview = "See you at the concert!",
                lastMessageType = MessageType.TEXT,
                lastMessageTime = now - 900_000,
                streakDays = 18,
                hasUnopenedSnap = false,
                isOnline = true
            ),
            ChatConversation(
                id = "conv_3",
                friendId = "friend_3",
                friendName = "Maya Patel",
                friendUsername = "mayap",
                avatarColorHex = 0xFF9C27B0,
                bitmojiMood = BitmojiMood.STUDY,
                lastMessagePreview = "New Snap Video",
                lastMessageType = MessageType.VIDEO,
                lastMessageTime = now - 3600_000,
                streakDays = 79,
                hasUnopenedSnap = true,
                isOnline = false
            ),
            ChatConversation(
                id = "conv_4",
                friendId = "friend_4",
                friendName = "Ethan Davis",
                friendUsername = "ethan_d",
                avatarColorHex = 0xFFFF9100,
                bitmojiMood = BitmojiMood.MUSIC,
                lastMessagePreview = "Shared a location pin",
                lastMessageType = MessageType.LOCATION_PIN,
                lastMessageTime = now - 7200_000,
                streakDays = 5,
                hasUnopenedSnap = false,
                isOnline = true
            )
        )

        _conversations.value = sampleConvs

        // Sample initial messages for conversation 1 (Sophia)
        val conv1Messages = listOf(
            Message(
                id = "m1",
                conversationId = "conv_1",
                senderId = "friend_1",
                senderName = "Sophia Chen",
                content = "Hey Alex! Check out this coffee shop near 4th street ☕",
                type = MessageType.TEXT,
                timestamp = now - 600_000,
                isFromMe = false
            ),
            Message(
                id = "m2",
                conversationId = "conv_1",
                senderId = "friend_1",
                senderName = "Sophia Chen",
                content = null,
                type = MessageType.IMAGE,
                // Using high quality royalty free Unsplash cafe sample
                mediaUri = "https://images.unsplash.com/photo-1501339847302-ac426a4a7cbb?auto=format&fit=crop&w=600&q=80",
                timestamp = now - 480_000,
                isFromMe = false,
                isOpened = false
            ),
            Message(
                id = "m3",
                conversationId = "conv_1",
                senderId = "user_me",
                senderName = "Alex",
                content = "Looks amazing! I saw you on SnapMap right by the plaza",
                type = MessageType.TEXT,
                timestamp = now - 300_000,
                isFromMe = true
            )
        )

        // Sample initial messages for conversation 2 (Liam)
        val conv2Messages = listOf(
            Message(
                id = "m4",
                conversationId = "conv_2",
                senderId = "friend_2",
                senderName = "Liam Walker",
                content = "Road trip playlist is ready! Check this clip from earlier 🎶",
                type = MessageType.TEXT,
                timestamp = now - 1800_000,
                isFromMe = false
            ),
            Message(
                id = "m5",
                conversationId = "conv_2",
                senderId = "friend_2",
                senderName = "Liam Walker",
                content = "Quick drive-by clip",
                type = MessageType.VIDEO,
                mediaUri = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
                mediaDurationSeconds = 15,
                timestamp = now - 1500_000,
                isFromMe = false,
                isOpened = true
            ),
            Message(
                id = "m6",
                conversationId = "conv_2",
                senderId = "user_me",
                senderName = "Alex",
                content = "See you at the concert!",
                type = MessageType.TEXT,
                timestamp = now - 900_000,
                isFromMe = true
            )
        )

        _messages.value = mapOf(
            "conv_1" to conv1Messages,
            "conv_2" to conv2Messages
        )
    }

    fun getMessagesForConversation(convId: String): List<Message> {
        return _messages.value[convId] ?: emptyList()
    }

    fun sendTextMessage(conversationId: String, text: String) {
        if (text.isBlank()) return
        val newMsg = Message(
            id = UUID.randomUUID().toString(),
            conversationId = conversationId,
            senderId = "user_me",
            senderName = "Alex",
            content = text.trim(),
            type = MessageType.TEXT,
            timestamp = System.currentTimeMillis(),
            isFromMe = true
        )
        appendMessage(newMsg)
        triggerSimulatedReply(conversationId, MessageType.TEXT)
    }

    fun sendImageMessage(conversationId: String, imageUri: String) {
        val newMsg = Message(
            id = UUID.randomUUID().toString(),
            conversationId = conversationId,
            senderId = "user_me",
            senderName = "Alex",
            type = MessageType.IMAGE,
            mediaUri = imageUri,
            timestamp = System.currentTimeMillis(),
            isFromMe = true,
            isOpened = true
        )
        appendMessage(newMsg)
        triggerSimulatedReply(conversationId, MessageType.IMAGE)
    }

    fun sendVideoMessage(conversationId: String, videoUri: String, durationSec: Int = 10) {
        val newMsg = Message(
            id = UUID.randomUUID().toString(),
            conversationId = conversationId,
            senderId = "user_me",
            senderName = "Alex",
            type = MessageType.VIDEO,
            mediaUri = videoUri,
            mediaDurationSeconds = durationSec,
            timestamp = System.currentTimeMillis(),
            isFromMe = true,
            isOpened = true
        )
        appendMessage(newMsg)
        triggerSimulatedReply(conversationId, MessageType.VIDEO)
    }

    fun sendLocationMessage(conversationId: String, lat: Double, lng: Double, label: String = "My Current Location") {
        val newMsg = Message(
            id = UUID.randomUUID().toString(),
            conversationId = conversationId,
            senderId = "user_me",
            senderName = "Alex",
            type = MessageType.LOCATION_PIN,
            locationLat = lat,
            locationLng = lng,
            locationName = label,
            timestamp = System.currentTimeMillis(),
            isFromMe = true
        )
        appendMessage(newMsg)
        triggerSimulatedReply(conversationId, MessageType.LOCATION_PIN)
    }

    fun markMessageAsOpened(conversationId: String, messageId: String) {
        val currentList = _messages.value[conversationId] ?: return
        val updated = currentList.map {
            if (it.id == messageId) it.copy(isOpened = true) else it
        }
        val currentMap = _messages.value.toMutableMap()
        currentMap[conversationId] = updated
        _messages.value = currentMap
    }

    private fun appendMessage(msg: Message) {
        val currentMap = _messages.value.toMutableMap()
        val list = currentMap[msg.conversationId]?.toMutableList() ?: mutableListOf()
        list.add(msg)
        currentMap[msg.conversationId] = list
        _messages.value = currentMap

        // Update conversation summary
        val preview = when (msg.type) {
            MessageType.TEXT -> msg.content ?: "Text message"
            MessageType.IMAGE -> "Sent a Photo"
            MessageType.VIDEO -> "Sent a Video"
            MessageType.LOCATION_PIN -> "Shared Location"
        }
        updateConversationPreview(msg.conversationId, preview, msg.type, msg.timestamp)
    }

    private fun updateConversationPreview(
        convId: String,
        preview: String,
        type: MessageType,
        time: Long
    ) {
        val convs = _conversations.value.map {
            if (it.id == convId) {
                it.copy(
                    lastMessagePreview = preview,
                    lastMessageType = type,
                    lastMessageTime = time
                )
            } else it
        }
        _conversations.value = convs
    }

    private fun triggerSimulatedReply(conversationId: String, triggeredBy: MessageType) {
        scope.launch {
            delay(2500)
            val conv = _conversations.value.firstOrNull { it.id == conversationId } ?: return@launch
            val replyText = when (triggeredBy) {
                MessageType.TEXT -> listOf(
                    "Haha totally! 😄",
                    "Awesome, sounds good to me!",
                    "Check SnapMap, I'm heading over in 10 mins!",
                    "Did you see what just happened? 🔥"
                ).random()
                MessageType.IMAGE -> "Love this snap! Looking fresh 📸✨"
                MessageType.VIDEO -> "Woah fire video! Where was this filmed?! 🎥🔥"
                MessageType.LOCATION_PIN -> "Got your pin! Just spotted you on SnapMap 🗺️📍"
            }

            val replyMsg = Message(
                id = UUID.randomUUID().toString(),
                conversationId = conversationId,
                senderId = conv.friendId,
                senderName = conv.friendName,
                content = replyText,
                type = MessageType.TEXT,
                timestamp = System.currentTimeMillis(),
                isFromMe = false
            )
            appendMessage(replyMsg)
        }
    }
}
