package com.snappulse.app.ui.chat

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.snappulse.app.data.model.Message
import com.snappulse.app.data.model.MessageType
import com.snappulse.app.ui.components.MediaViewerDialog
import com.snappulse.app.ui.components.VideoPlayerDialog
import com.snappulse.app.ui.theme.SnapBlack
import com.snappulse.app.ui.theme.SnapBlueChat
import com.snappulse.app.ui.theme.SnapDarkGray
import com.snappulse.app.ui.theme.SnapOrangeStreak
import com.snappulse.app.ui.theme.SnapPurpleVideo
import com.snappulse.app.ui.theme.SnapRedSnap
import com.snappulse.app.ui.theme.SnapSurface
import com.snappulse.app.ui.theme.SnapSurfaceVariant
import com.snappulse.app.ui.theme.SnapYellow
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun ChatDetailScreen(
    conversationId: String,
    onBack: () -> Unit,
    onNavigateToMap: (String) -> Unit,
    viewModel: ChatViewModel = viewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    LaunchedEffect(conversationId) {
        viewModel.selectConversation(conversationId)
    }

    val conversation = viewModel.getConversation(conversationId)
    val messages by viewModel.currentMessages.collectAsState()

    var textInput by remember { mutableStateOf("") }
    var showMediaPicker by remember { mutableStateOf(false) }

    // Media fullscreen viewer state
    var viewingImageUrl by remember { mutableStateOf<String?>(null) }
    var viewingVideoUrl by remember { mutableStateOf<String?>(null) }

    // Temp URI for camera capture
    var tempPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var tempVideoUri by remember { mutableStateOf<Uri?>(null) }

    // Launchers
    val takePhotoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempPhotoUri != null) {
            viewModel.sendImage(conversationId, tempPhotoUri.toString())
        }
    }

    val recordVideoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CaptureVideo()
    ) { success ->
        if (success && tempVideoUri != null) {
            viewModel.sendVideo(conversationId, tempVideoUri.toString())
        }
    }

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { viewModel.sendImage(conversationId, it.toString()) }
    }

    val pickVideoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { viewModel.sendVideo(conversationId, it.toString()) }
    }

    // Scroll to bottom on new message
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SnapBlack)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
    ) {
        // Chat Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            // Friend Avatar
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(conversation?.avatarColorHex ?: 0xFFFFFC00)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = conversation?.bitmojiMood?.emoji ?: "😎", fontSize = 22.sp)
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = conversation?.friendName ?: "Friend",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "@${conversation?.friendUsername ?: "friend"} • ${if (conversation?.isOnline == true) "Online" else "Active recently"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 11.sp
                )
            }

            // Streak Badge
            if ((conversation?.streakDays ?: 0) > 0) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(SnapSurface)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "🔥 ${conversation?.streakDays}",
                        color = SnapOrangeStreak,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
            }

            // SnapMap Quick Button
            IconButton(
                onClick = { onNavigateToMap(conversation?.friendId ?: "") },
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(SnapSurface)
            ) {
                Icon(
                    imageVector = Icons.Default.Map,
                    contentDescription = "View on SnapMap",
                    tint = SnapYellow,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Divider(color = SnapDarkGray, thickness = 1.dp)

        // Messages Timeline
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(messages, key = { it.id }) { msg ->
                MessageBubbleItem(
                    message = msg,
                    onImageClick = { url -> viewingImageUrl = url },
                    onVideoClick = { url -> viewingVideoUrl = url },
                    onLocationClick = { onNavigateToMap(conversation?.friendId ?: "") }
                )
            }
        }

        // Bottom Input Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SnapDarkGray)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Media Attachment Button (+)
            IconButton(
                onClick = { showMediaPicker = true },
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(SnapSurface)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Attach Media",
                    tint = SnapYellow,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Quick Camera Snap Button
            IconButton(
                onClick = {
                    val file = File(context.cacheDir, "snap_${System.currentTimeMillis()}.jpg")
                    val uri = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.fileprovider",
                        file
                    )
                    tempPhotoUri = uri
                    takePhotoLauncher.launch(uri)
                },
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(SnapSurface)
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Camera",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Text input field
            OutlinedTextField(
                value = textInput,
                onValueChange = { textInput = it },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                placeholder = {
                    Text(
                        text = "Send a chat...",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 14.sp
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SnapBlueChat,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = SnapSurface,
                    unfocusedContainerColor = SnapSurface,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(24.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(
                    onSend = {
                        if (textInput.isNotBlank()) {
                            viewModel.sendText(conversationId, textInput)
                            textInput = ""
                        }
                    }
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Send Button
            IconButton(
                onClick = {
                    if (textInput.isNotBlank()) {
                        viewModel.sendText(conversationId, textInput)
                        textInput = ""
                    }
                },
                enabled = textInput.isNotBlank(),
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(if (textInput.isNotBlank()) SnapBlueChat else SnapSurface)
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send",
                    tint = if (textInput.isNotBlank()) Color.White else Color.White.copy(alpha = 0.4f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }

    // Media Picker Bottom Sheet
    if (showMediaPicker) {
        MediaPickerSheet(
            onTakePhoto = {
                val file = File(context.cacheDir, "snap_${System.currentTimeMillis()}.jpg")
                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )
                tempPhotoUri = uri
                takePhotoLauncher.launch(uri)
            },
            onRecordVideo = {
                val file = File(context.cacheDir, "snap_${System.currentTimeMillis()}.mp4")
                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )
                tempVideoUri = uri
                recordVideoLauncher.launch(uri)
            },
            onPickImage = {
                pickImageLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            },
            onPickVideo = {
                pickVideoLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                )
            },
            onShareLocation = {
                viewModel.sendCurrentLocation(conversationId)
            },
            onDismiss = { showMediaPicker = false }
        )
    }

    // Fullscreen Viewers
    viewingImageUrl?.let { url ->
        MediaViewerDialog(imageUrl = url, onDismiss = { viewingImageUrl = null })
    }

    viewingVideoUrl?.let { url ->
        VideoPlayerDialog(videoUri = url, onDismiss = { viewingVideoUrl = null })
    }
}

@Composable
private fun MessageBubbleItem(
    message: Message,
    onImageClick: (String) -> Unit,
    onVideoClick: (String) -> Unit,
    onLocationClick: () -> Unit
) {
    val isMe = message.isFromMe

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
    ) {
        Column(
            horizontalAlignment = if (isMe) Alignment.End else Alignment.Start,
            modifier = Modifier.widthIn(max = 290.dp)
        ) {
            when (message.type) {
                MessageType.TEXT -> {
                    Box(
                        modifier = Modifier
                            .clip(
                                RoundedCornerShape(
                                    topStart = 18.dp,
                                    topEnd = 18.dp,
                                    bottomStart = if (isMe) 18.dp else 4.dp,
                                    bottomEnd = if (isMe) 4.dp else 18.dp
                                )
                            )
                            .background(if (isMe) SnapBlueChat else SnapSurfaceVariant)
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = message.content.orEmpty(),
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 15.sp
                        )
                    }
                }

                MessageType.IMAGE -> {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(18.dp))
                            .background(SnapDarkGray)
                            .clickable { message.mediaUri?.let { onImageClick(it) } }
                    ) {
                        AsyncImage(
                            model = message.mediaUri,
                            contentDescription = "Snap Image",
                            modifier = Modifier
                                .width(240.dp)
                                .height(280.dp),
                            contentScale = ContentScale.Crop
                        )
                        // Snap Photo Badge
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(8.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(SnapRedSnap)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "📸 Snap Photo",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                MessageType.VIDEO -> {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(18.dp))
                            .background(SnapDarkGray)
                            .clickable { message.mediaUri?.let { onVideoClick(it) } }
                    ) {
                        // Video thumbnail or placeholder
                        AsyncImage(
                            model = message.mediaUri,
                            contentDescription = "Snap Video",
                            modifier = Modifier
                                .width(240.dp)
                                .height(280.dp)
                                .background(SnapSurface),
                            contentScale = ContentScale.Crop
                        )

                        // Central Play Button Overlay
                        Box(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(54.dp)
                                .clip(CircleShape)
                                .background(SnapPurpleVideo.copy(alpha = 0.9f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Play",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        // Video Badge
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(8.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(SnapPurpleVideo)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "🎥 Video Snap",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                MessageType.LOCATION_PIN -> {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(18.dp))
                            .background(SnapSurface)
                            .clickable(onClick = onLocationClick)
                            .padding(14.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(SnapYellow),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = SnapBlack,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = message.locationName ?: "Shared Location",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Tap to open on Snap Map 🗺️",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = SnapYellow,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(3.dp))

            // Time & Status
            Text(
                text = "${message.formattedTime} • ${message.deliveredStatus}",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 10.sp
            )
        }
    }
}
