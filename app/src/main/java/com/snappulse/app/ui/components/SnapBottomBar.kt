package com.snappulse.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.snappulse.app.navigation.Screen
import com.snappulse.app.ui.theme.SnapBlack
import com.snappulse.app.ui.theme.SnapDarkGray
import com.snappulse.app.ui.theme.SnapYellow

@Composable
fun SnapBottomBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(SnapBlack.copy(alpha = 0.95f))
            .navigationBarsPadding()
            .height(64.dp)
            .padding(horizontal = 24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Map
            BottomNavItem(
                icon = Icons.Default.Map,
                isSelected = currentRoute == Screen.SnapMap.route,
                onClick = { onNavigate(Screen.SnapMap.route) }
            )

            // Chat
            BottomNavItem(
                icon = Icons.Default.ChatBubbleOutline,
                isSelected = currentRoute == Screen.ChatList.route,
                onClick = { onNavigate(Screen.ChatList.route) }
            )

            // Camera Center Button (Snapchat shutter style)
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(SnapYellow)
                    .clickable { onNavigate(Screen.Camera.route) },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Camera Snap",
                    tint = SnapBlack,
                    modifier = Modifier.size(28.dp)
                )
            }

            // Profile / Settings
            BottomNavItem(
                icon = Icons.Default.Person,
                isSelected = currentRoute == Screen.Profile.route,
                onClick = { onNavigate(Screen.Profile.route) }
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val tint by animateColorAsState(
        targetValue = if (isSelected) SnapYellow else Color.White.copy(alpha = 0.6f),
        label = "nav_tint"
    )

    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(26.dp)
        )
    }
}
