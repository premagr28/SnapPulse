package com.snappulse.app.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.snappulse.app.ui.theme.SnapBlueChat
import com.snappulse.app.ui.theme.SnapDarkGray
import com.snappulse.app.ui.theme.SnapPurpleVideo
import com.snappulse.app.ui.theme.SnapRedSnap
import com.snappulse.app.ui.theme.SnapSurface
import com.snappulse.app.ui.theme.SnapYellow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaPickerSheet(
    onTakePhoto: () -> Unit,
    onRecordVideo: () -> Unit,
    onPickImage: () -> Unit,
    onPickVideo: () -> Unit,
    onShareLocation: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SnapDarkGray,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp)
        ) {
            Text(
                text = "Send Media & Location",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Option 1: Take Photo
            MediaOptionItem(
                icon = Icons.Default.CameraAlt,
                iconTint = SnapRedSnap,
                title = "Take Snap Photo",
                subtitle = "Capture a picture right now",
                onClick = {
                    onDismiss()
                    onTakePhoto()
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Option 2: Record Video
            MediaOptionItem(
                icon = Icons.Default.Videocam,
                iconTint = SnapPurpleVideo,
                title = "Record Video Snap",
                subtitle = "Shoot a quick live video",
                onClick = {
                    onDismiss()
                    onRecordVideo()
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Option 3: Pick Photo from Gallery
            MediaOptionItem(
                icon = Icons.Default.PhotoLibrary,
                iconTint = SnapBlueChat,
                title = "Choose Photo from Gallery",
                subtitle = "Select an image from device memory",
                onClick = {
                    onDismiss()
                    onPickImage()
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Option 4: Pick Video from Gallery
            MediaOptionItem(
                icon = Icons.Default.VideoLibrary,
                iconTint = SnapPurpleVideo,
                title = "Choose Video from Gallery",
                subtitle = "Select a video from device memory",
                onClick = {
                    onDismiss()
                    onPickVideo()
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Option 5: Send Live Location Pin
            MediaOptionItem(
                icon = Icons.Default.LocationOn,
                iconTint = SnapYellow,
                title = "Send Current Location Pin",
                subtitle = "Share your live GPS coordinates with friend",
                onClick = {
                    onDismiss()
                    onShareLocation()
                }
            )

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
private fun MediaOptionItem(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SnapSurface)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(iconTint.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 11.sp
            )
        }
    }
}
