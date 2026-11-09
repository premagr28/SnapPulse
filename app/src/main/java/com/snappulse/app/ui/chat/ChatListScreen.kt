package com.snappulse.app.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.snappulse.app.data.model.ChatConversation
import com.snappulse.app.data.model.MessageType
import com.snappulse.app.ui.theme.SnapBlack
import com.snappulse.app.ui.theme.SnapBlueChat
import com.snappulse.app.ui.theme.SnapDarkGray
import com.snappulse.app.ui.theme.SnapGreenOnline
import com.snappulse.app.ui.theme.SnapOrangeStreak
import com.snappulse.app.ui.theme.SnapPurpleVideo
import com.snappulse.app.ui.theme.SnapRedSnap
import com.snappulse.app.ui.theme.SnapSurface
import com.snappulse.app.ui.theme.SnapYellow

@Composable
fun ChatListScreen(
    onNavigateToChat: (String) -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToCamera: () -> Unit,
    viewModel: ChatViewModel = viewModel()
) {
    val conversations by viewModel.conversations.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SnapBlack)
            .statusBarsPadding()
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Profile Bitmoji Button
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(SnapYellow)
                    .clickable(onClick = onNavigateToProfile),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "😎", fontSize = 20.sp)
            }

            Text(
                text = "Chat",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Row {
                IconButton(
                    onClick = { },
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(SnapSurface)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = { },
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(SnapSurface)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "New Chat",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Divider(color = SnapDarkGray, thickness = 1.dp)

        // Conversation List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            items(conversations, key = { it.id }) { conv ->
                ChatConversationRow(
                    conversation = conv,
                    onClick = { onNavigateToChat(conv.id) },
                    onCameraClick = onNavigateToCamera
                )
                Divider(
                    color = SnapDarkGray.copy(alpha = 0.5f),
                    thickness = 0.8.dp,
                    modifier = Modifier.padding(start = 74.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(64.dp))
    }
}

@Composable
private fun ChatConversationRow(
    conversation: ChatConversation,
    onClick: () -> Unit,
    onCameraClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Bitmoji Avatar with status ring
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(Color(conversation.avatarColorHex)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = conversation.bitmojiMood.emoji, fontSize = 26.sp)

            if (conversation.isOnline) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(SnapGreenOnline)
                        .align(Alignment.BottomEnd)
                )
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

        // Name and Last message info
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = conversation.friendName,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Snap status indicator icon
                SnapStatusBadge(
                    messageType = conversation.lastMessageType,
                    hasUnopened = conversation.hasUnopenedSnap
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = "${conversation.lastMessagePreview} • ${conversation.formattedTime}",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (conversation.hasUnopenedSnap) Color.White else Color.White.copy(alpha = 0.6f),
                    fontWeight = if (conversation.hasUnopenedSnap) FontWeight.Bold else FontWeight.Normal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // Streak & Quick Camera
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (conversation.streakDays > 0) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(SnapSurface)
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🔥 ${conversation.streakDays}",
                        color = SnapOrangeStreak,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
            }

            IconButton(
                onClick = onCameraClick,
                modifier = Modifier.size(34.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Quick Snap",
                    tint = Color.White.copy(alpha = 0.6f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun SnapStatusBadge(
    messageType: MessageType,
    hasUnopened: Boolean
) {
    val color = when (messageType) {
        MessageType.TEXT -> SnapBlueChat
        MessageType.IMAGE -> SnapRedSnap
        MessageType.VIDEO -> SnapPurpleVideo
        MessageType.LOCATION_PIN -> SnapYellow
    }

    if (hasUnopened) {
        // Solid square / badge
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(color)
        )
    } else {
        // Hollow / outline
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(Color.Transparent)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(3.dp))
                    .background(color.copy(alpha = 0.35f))
            )
        }
    }
}
