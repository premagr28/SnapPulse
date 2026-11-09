package com.snappulse.app.ui.permissions

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.snappulse.app.data.repository.PermissionsManager
import com.snappulse.app.ui.theme.SnapBlack
import com.snappulse.app.ui.theme.SnapDarkGray
import com.snappulse.app.ui.theme.SnapGreenOnline
import com.snappulse.app.ui.theme.SnapSurface
import com.snappulse.app.ui.theme.SnapYellow

@Composable
fun PermissionOnboardingScreen(
    onPermissionsCompleted: () -> Unit
) {
    val context = LocalContext.current

    var hasLocation by remember { mutableStateOf(PermissionsManager.hasLocationPermission(context)) }
    var hasCamera by remember { mutableStateOf(PermissionsManager.hasCameraPermission(context)) }
    var hasMedia by remember { mutableStateOf(PermissionsManager.hasMediaPermission(context)) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        hasLocation = PermissionsManager.hasLocationPermission(context)
        hasCamera = PermissionsManager.hasCameraPermission(context)
        hasMedia = PermissionsManager.hasMediaPermission(context)

        if (hasLocation || hasCamera) {
            onPermissionsCompleted()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SnapBlack)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Snap Logo / Ghost Icon
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(SnapYellow),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "👻",
                    fontSize = 42.sp
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Welcome to SnapPulse",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Track friends on Snap Map and chat with live media.\nEnable permissions to get started.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Permission Items
            PermissionCard(
                icon = Icons.Default.LocationOn,
                title = "Precise Location",
                description = "Shows your live Bitmoji on Snap Map and displays nearby friends.",
                isGranted = hasLocation
            )

            Spacer(modifier = Modifier.height(12.dp))

            PermissionCard(
                icon = Icons.Default.CameraAlt,
                title = "Camera & Audio",
                description = "Capture snaps and record video messages to share in chats.",
                isGranted = hasCamera
            )

            Spacer(modifier = Modifier.height(12.dp))

            PermissionCard(
                icon = Icons.Default.PhotoLibrary,
                title = "Photos & Videos",
                description = "Pick and send images & recorded clips directly inside chats.",
                isGranted = hasMedia
            )

            Spacer(modifier = Modifier.height(12.dp))

            PermissionCard(
                icon = Icons.Default.Notifications,
                title = "Notifications",
                description = "Receive instant alerts for incoming chats, snaps, and friend updates.",
                isGranted = true
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // CTA Actions
        Column(modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = {
                    permissionLauncher.launch(PermissionsManager.allOnboardingPermissions)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SnapYellow,
                    contentColor = SnapBlack
                ),
                shape = RoundedCornerShape(27.dp)
            ) {
                Text(
                    text = if (hasLocation && hasCamera) "All Set! Enter SnapPulse" else "Enable All Permissions",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = {
                    PermissionsManager.openAppSettings(context)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.White.copy(alpha = 0.8f)
                ),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text(text = "Manage in Device Settings", fontSize = 14.sp)
            }
        }
    }
}

@Composable
private fun PermissionCard(
    icon: ImageVector,
    title: String,
    description: String,
    isGranted: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = SnapSurface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (isGranted) SnapGreenOnline.copy(alpha = 0.2f) else SnapYellow.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isGranted) SnapGreenOnline else SnapYellow,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.65f),
                    lineHeight = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            if (isGranted) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Granted",
                    tint = SnapGreenOnline,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}
