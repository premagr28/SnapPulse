package com.snappulse.app.ui.map

import android.location.Location
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.snappulse.app.data.model.FriendLocation
import com.snappulse.app.data.repository.PermissionsManager
import com.snappulse.app.ui.theme.SnapBlack
import com.snappulse.app.ui.theme.SnapBlueChat
import com.snappulse.app.ui.theme.SnapDarkGray
import com.snappulse.app.ui.theme.SnapGreenOnline
import com.snappulse.app.ui.theme.SnapSurface
import com.snappulse.app.ui.theme.SnapYellow
import kotlinx.coroutines.launch

@Composable
fun SnapMapScreen(
    onNavigateToChat: (String) -> Unit,
    onNavigateToProfile: () -> Unit,
    viewModel: SnapMapViewModel = viewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val userProfile by viewModel.userProfile.collectAsState()
    val userLocation by viewModel.userLocation.collectAsState()
    val friends by viewModel.friends.collectAsState()
    val selectedFriend by viewModel.selectedFriend.collectAsState()
    val showGhostModeSheet by viewModel.showGhostModeSheet.collectAsState()

    val initialPos = LatLng(userProfile.currentLat, userProfile.currentLng)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialPos, 14.5f)
    }

    var showKeyBanner by remember { mutableStateOf(!viewModel.isApiKeyConfigured) }
    val friendListState = rememberLazyListState()

    // Smoothly pan camera when user location is first updated
    LaunchedEffect(userLocation) {
        userLocation?.let { loc ->
            if (selectedFriend == null) {
                cameraPositionState.animate(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(loc.latitude, loc.longitude),
                        15f
                    )
                )
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Google Maps View
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                isMyLocationEnabled = PermissionsManager.hasLocationPermission(context),
                mapStyleOptions = MapStyleOptions(SnapMapStyle.JSON_STYLE)
            ),
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false,
                myLocationButtonEnabled = false,
                compassEnabled = false,
                mapToolbarEnabled = false
            ),
            onMapClick = {
                viewModel.selectFriend(null)
            }
        ) {
            // User Avatar Marker (if not in Ghost Mode)
            if (!userProfile.isGhostMode) {
                val myLatLng = LatLng(userProfile.currentLat, userProfile.currentLng)
                MarkerComposable(
                    keys = arrayOf("user_marker", userProfile.currentLat, userProfile.currentLng),
                    state = MarkerState(position = myLatLng),
                    title = "You",
                    snippet = userProfile.currentStatus
                ) {
                    UserMapAvatar(status = userProfile.currentStatus)
                }
            }

            // Friend Bitmoji Markers
            friends.forEach { friend ->
                val friendLatLng = LatLng(friend.latitude, friend.longitude)
                MarkerComposable(
                    keys = arrayOf(friend.id, friend.latitude, friend.longitude, selectedFriend?.id == friend.id),
                    state = MarkerState(position = friendLatLng),
                    title = friend.name,
                    snippet = friend.statusText,
                    onClick = {
                        viewModel.selectFriend(friend)
                        coroutineScope.launch {
                            cameraPositionState.animate(
                                CameraUpdateFactory.newLatLngZoom(friendLatLng, 16.5f)
                            )
                        }
                        true
                    }
                ) {
                    FriendMapAvatar(
                        friend = friend,
                        isSelected = selectedFriend?.id == friend.id
                    )
                }
            }
        }

        // Top Gradient overlay
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(SnapBlack.copy(alpha = 0.85f), Color.Transparent)
                    )
                )
        )

        // Top Header Bar
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Profile Avatar Button
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(SnapYellow)
                        .clickable(onClick = onNavigateToProfile),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "😎", fontSize = 22.sp)
                }

                // SnapMap Title & Search Pill
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp)
                        .height(42.dp)
                        .clip(RoundedCornerShape(21.dp))
                        .background(SnapDarkGray.copy(alpha = 0.9f))
                        .clickable { },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Snap Map • ${friends.size} friends online",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Ghost Mode Pill
                Box(
                    modifier = Modifier
                        .height(38.dp)
                        .clip(RoundedCornerShape(19.dp))
                        .background(if (userProfile.isGhostMode) SnapBlueChat else SnapSurface.copy(alpha = 0.9f))
                        .clickable { viewModel.setShowGhostModeSheet(true) }
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "👻", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (userProfile.isGhostMode) "Ghost ON" else "Ghost",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Notice Banner if API Key is not configured
            AnimatedVisibility(visible = showKeyBanner) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SnapDarkGray.copy(alpha = 0.95f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = SnapYellow,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Google Maps API Key Setup",
                                style = MaterialTheme.typography.titleSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Add your key in local.properties: MAPS_API_KEY=AIzaSy... All live UI features and friend simulations are active!",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 11.sp
                            )
                        }
                        IconButton(onClick = { showKeyBanner = false }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Dismiss",
                                tint = Color.White.copy(alpha = 0.7f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        // Floating Recenter Button
        FloatingActionButton(
            onClick = {
                val targetLat = userLocation?.latitude ?: userProfile.currentLat
                val targetLng = userLocation?.longitude ?: userProfile.currentLng
                coroutineScope.launch {
                    cameraPositionState.animate(
                        CameraUpdateFactory.newLatLngZoom(LatLng(targetLat, targetLng), 16f)
                    )
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 220.dp, end = 16.dp)
                .size(50.dp),
            containerColor = SnapDarkGray,
            contentColor = SnapYellow,
            shape = CircleShape
        ) {
            Icon(
                imageVector = Icons.Default.MyLocation,
                contentDescription = "My Location",
                modifier = Modifier.size(24.dp)
            )
        }

        // Bottom Friend Carousel
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 76.dp)
                .fillMaxWidth()
        ) {
            LazyRow(
                state = friendListState,
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(friends, key = { it.id }) { friend ->
                    FriendCard(
                        friend = friend,
                        userLat = userProfile.currentLat,
                        userLng = userProfile.currentLng,
                        isSelected = selectedFriend?.id == friend.id,
                        onCardClick = {
                            viewModel.selectFriend(friend)
                            coroutineScope.launch {
                                cameraPositionState.animate(
                                    CameraUpdateFactory.newLatLngZoom(
                                        LatLng(friend.latitude, friend.longitude),
                                        16.5f
                                    )
                                )
                            }
                        },
                        onChatClick = {
                            // Find or create conversation with this friend
                            val convId = when (friend.id) {
                                "friend_1" -> "conv_1"
                                "friend_2" -> "conv_2"
                                "friend_3" -> "conv_3"
                                "friend_4" -> "conv_4"
                                else -> "conv_1"
                            }
                            onNavigateToChat(convId)
                        }
                    )
                }
            }
        }

        // Ghost Mode Bottom Sheet Modal
        if (showGhostModeSheet) {
            GhostModeSheet(
                userProfile = userProfile,
                onToggleGhostMode = { enabled, duration ->
                    viewModel.toggleGhostMode(enabled, duration)
                },
                onDismiss = { viewModel.setShowGhostModeSheet(false) }
            )
        }
    }
}

@Composable
private fun UserMapAvatar(status: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(SnapYellow)
                .border(3.dp, Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "😎", fontSize = 28.sp)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(SnapBlack.copy(alpha = 0.85f))
                .padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
            Text(
                text = "You • Active",
                color = SnapYellow,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun FriendMapAvatar(
    friend: FriendLocation,
    isSelected: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(if (isSelected) 56.dp else 48.dp)
                .clip(CircleShape)
                .background(Color(friend.avatarColorHex))
                .border(
                    width = if (isSelected) 3.dp else 2.dp,
                    color = if (isSelected) SnapYellow else Color.White,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(text = friend.bitmojiMood.emoji, fontSize = if (isSelected) 28.sp else 24.sp)
        }
        Spacer(modifier = Modifier.height(3.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(SnapBlack.copy(alpha = 0.85f))
                .border(
                    width = 1.dp,
                    color = if (isSelected) SnapYellow else Color.Transparent,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 7.dp, vertical = 2.dp)
        ) {
            Text(
                text = friend.name.substringBefore(" "),
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun FriendCard(
    friend: FriendLocation,
    userLat: Double,
    userLng: Double,
    isSelected: Boolean,
    onCardClick: () -> Unit,
    onChatClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(260.dp)
            .clickable(onClick = onCardClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) SnapSurface else SnapDarkGray.copy(alpha = 0.95f)
        ),
        border = if (isSelected) CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(SnapYellow, SnapBlueChat))) else null
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Bitmoji Avatar
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color(friend.avatarColorHex)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = friend.bitmojiMood.emoji, fontSize = 24.sp)
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = friend.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${friend.distanceTo(userLat, userLng)} • ${friend.lastSeenTimeAgo}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.65f),
                        fontSize = 11.sp
                    )
                }

                // Battery Status
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (friend.isCharging) "⚡${friend.batteryPercent}%" else "${friend.batteryPercent}%",
                        color = if (friend.batteryPercent < 20) Color.Red else SnapGreenOnline,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Status message
            Text(
                text = friend.statusText,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.85f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            if (friend.speedKmh > 10f) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "🚗 In motion: ${friend.speedKmh.toInt()} km/h",
                    style = MaterialTheme.typography.bodySmall,
                    color = SnapBlueChat,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onChatClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SnapBlueChat,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(19.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Chat,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Chat", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
