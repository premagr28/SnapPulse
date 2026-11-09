package com.snappulse.app.ui.map

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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.snappulse.app.data.model.UserProfile
import com.snappulse.app.ui.theme.SnapBlack
import com.snappulse.app.ui.theme.SnapDarkGray
import com.snappulse.app.ui.theme.SnapSurface
import com.snappulse.app.ui.theme.SnapYellow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GhostModeSheet(
    userProfile: UserProfile,
    onToggleGhostMode: (Boolean, String) -> Unit,
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
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(SnapYellow.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "👻", fontSize = 24.sp)
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = "Ghost Mode",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (userProfile.isGhostMode) "Active (${userProfile.ghostModeDurationText})" else "Disabled",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (userProfile.isGhostMode) SnapYellow else Color.White.copy(alpha = 0.5f)
                        )
                    }
                }

                Switch(
                    checked = userProfile.isGhostMode,
                    onCheckedChange = { checked ->
                        onToggleGhostMode(checked, if (checked) "Until turned off" else "Off")
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = SnapBlack,
                        checkedTrackColor = SnapYellow,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = SnapSurface
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "When Ghost Mode is enabled, your friends cannot see your location on Snap Map. Your Bitmoji stays frozen or disappears.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.7f),
                lineHeight = 20.sp
            )

            if (userProfile.isGhostMode) {
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Timer Duration",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(10.dp))

                DurationOption(
                    title = "3 Hours",
                    isSelected = userProfile.ghostModeDurationText == "3 Hours",
                    onClick = { onToggleGhostMode(true, "3 Hours") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                DurationOption(
                    title = "24 Hours",
                    isSelected = userProfile.ghostModeDurationText == "24 Hours",
                    onClick = { onToggleGhostMode(true, "24 Hours") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                DurationOption(
                    title = "Until turned off",
                    isSelected = userProfile.ghostModeDurationText == "Until turned off",
                    onClick = { onToggleGhostMode(true, "Until turned off") }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun DurationOption(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SnapSurface)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = SnapYellow,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
