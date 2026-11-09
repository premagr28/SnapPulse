package com.snappulse.app.ui.profile

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.snappulse.app.SnapPulseApp
import com.snappulse.app.data.model.BitmojiMood
import com.snappulse.app.data.repository.PermissionsManager
import com.snappulse.app.service.LocationTrackingService
import com.snappulse.app.ui.theme.SnapBlack
import com.snappulse.app.ui.theme.SnapBlueChat
import com.snappulse.app.ui.theme.SnapDarkGray
import com.snappulse.app.ui.theme.SnapGreenOnline
import com.snappulse.app.ui.theme.SnapOrangeStreak
import com.snappulse.app.ui.theme.SnapSurface
import com.snappulse.app.ui.theme.SnapYellow

@Composable
fun ProfileScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val locationRepo = SnapPulseApp.instance.locationRepository
    val userProfile by locationRepo.userProfile.collectAsState()

    var statusInput by remember { mutableStateOf(userProfile.currentStatus) }
    var selectedMood by remember { mutableStateOf(userProfile.bitmojiMood) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SnapBlack)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            Text(
                text = "My Profile",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = { PermissionsManager.openAppSettings(context) }) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Snapcode Yellow Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = SnapYellow)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Bitmoji Center
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(SnapBlack),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = selectedMood.emoji, fontSize = 48.sp)
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = userProfile.name,
                    style = MaterialTheme.typography.headlineMedium,
                    color = SnapBlack,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "@${userProfile.username}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SnapBlack.copy(alpha = 0.7f),
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Snap Score Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(SnapBlack)
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "🔥 ${userProfile.snapScore} Snap Score",
                        color = SnapYellow,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Bitmoji Mood Selector
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text(
                text = "Choose Your Bitmoji Mood",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                BitmojiMood.values().forEach { mood ->
                    val isSelected = selectedMood == mood
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) SnapYellow else SnapSurface)
                            .clickable {
                                selectedMood = mood
                                locationRepo.updateUserStatus(statusInput, mood)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = mood.emoji, fontSize = 22.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Snap Map Settings Section
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text(
                text = "Snap Map Location Settings",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Ghost Mode Toggle Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SnapDarkGray)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "👻", fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Ghost Mode",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (userProfile.isGhostMode) "Location hidden from friends" else "Location visible to friends",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.6f)
                                )
                            }
                        }

                        Switch(
                            checked = userProfile.isGhostMode,
                            onCheckedChange = { checked ->
                                locationRepo.setGhostMode(checked)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = SnapBlack,
                                checkedTrackColor = SnapYellow
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Divider(color = SnapSurface, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Current GPS: ${String.format("%.4f", userProfile.currentLat)}, ${String.format("%.4f", userProfile.currentLng)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = SnapGreenOnline,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Permissions Check Status
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text(
                text = "App Permissions Status",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SnapDarkGray)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    PermissionStatusRow(
                        icon = Icons.Default.LocationOn,
                        label = "Snap Map Location Tracking",
                        isGranted = PermissionsManager.hasLocationPermission(context)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    PermissionStatusRow(
                        icon = Icons.Default.CameraAlt,
                        label = "Camera & Audio Snaps",
                        isGranted = PermissionsManager.hasCameraPermission(context)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    PermissionStatusRow(
                        icon = Icons.Default.Key,
                        label = "Google Maps API Key",
                        isGranted = true
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    LocationTrackingService.start(context)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SnapSurface,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text(text = "Start Background Location Service")
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
private fun PermissionStatusRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isGranted: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isGranted) SnapYellow else Color.Gray,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = label,
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Text(
            text = if (isGranted) "Active" else "Missing",
            color = if (isGranted) SnapGreenOnline else Color.Red,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
