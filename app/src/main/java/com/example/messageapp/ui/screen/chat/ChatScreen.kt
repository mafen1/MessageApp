package com.example.messageapp.ui.screen.chat

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.messageapp.domain.model.Message
import com.example.messageapp.domain.model.MessageStatus
import com.example.messageapp.domain.model.SocketState
import com.example.messageapp.ui.components.imageUrl

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    currentUserName: String,
    currentName: String,
    otherUserName: String,
    otherName: String,
    onNavigateBack: () -> Unit,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val messages by viewModel.messageList.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    val connectionState by viewModel.connectionState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val listState = rememberLazyListState()
    var inputText by remember { mutableStateOf("") }

    val context = LocalContext.current
    // выбранная, но ещё не отправленная картинка: сначала превью с подтверждением
    var pendingImageUri by remember { mutableStateOf<android.net.Uri?>(null) }

    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            pendingImageUri = uri
        }
    }
    val confirmImageSend = {
        val uri = pendingImageUri
        if (uri != null) {
            val bytes = context.contentResolver.openInputStream(uri)?.use { stream ->
                val chunk = ByteArray(MAX_IMAGE_BYTES + 1)
                var total = 0
                while (total <= MAX_IMAGE_BYTES) {
                    val read = stream.read(chunk, total, chunk.size - total)
                    if (read <= 0) break
                    total += read
                }
                if (total > MAX_IMAGE_BYTES) null else chunk.copyOf(total)
            }
            if (bytes != null) {
                viewModel.sendImageMessage(otherUserName, bytes)
            } else {
                viewModel.showError("Файл слишком большой (лимит 10 МБ)")
            }
            pendingImageUri = null
        }
        Unit
    }

    LaunchedEffect(error) {
        error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.resetError()
        }
    }

    LaunchedEffect(messages) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    LaunchedEffect(currentUserName, otherUserName) {
        // сокет живёт на уровне приложения (Singleton): не рвём его при выходе с экрана,
        // чтобы входящие продолжали сохраняться в Room
        viewModel.connect(currentUserName)
        viewModel.loadMessageHistory(currentUserName, otherUserName)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(text = otherName, style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = connectionStateLabel(connectionState),
                            style = MaterialTheme.typography.bodySmall,
                            color = connectionStateColor(connectionState)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceContainer)
            ) {
                pendingImageUri?.let { uri ->
                    // превью выбранной картинки до отправки
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = uri,
                            contentDescription = null,
                            modifier = Modifier
                                .size(72.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Text(
                            text = "Отправить это изображение?",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 12.dp)
                        )
                        IconButton(onClick = { pendingImageUri = null }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Отменить",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                        IconButton(onClick = confirmImageSend) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Отправить",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                IconButton(onClick = { imagePicker.launch("image/*") }) {
                    Icon(
                        imageVector = Icons.Default.AttachFile,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text("Напишите сообщение…") },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    maxLines = 4,
                    shape = RoundedCornerShape(20.dp)
                )
                IconButton(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            viewModel.sendTextMessage(otherUserName, inputText)
                            inputText = ""
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(messages, key = { it.clientMessageId.ifBlank { "idx-${it.hashCode()}" } }) { message ->
                    ChatBubble(message = message)
                }
            }
        }
    }
}

@Composable
private fun connectionStateLabel(state: SocketState): String {
    return when (state) {
        is SocketState.Disconnected -> "Отключено"
        is SocketState.Connecting -> "Подключение…"
        is SocketState.Connected, is SocketState.Authenticated -> "В сети"
        is SocketState.Error -> "Ошибка соединения"
    }
}

@Composable
private fun connectionStateColor(state: SocketState) = when (state) {
    is SocketState.Disconnected, is SocketState.Error -> MaterialTheme.colorScheme.error
    is SocketState.Connecting -> MaterialTheme.colorScheme.primary
    is SocketState.Connected, is SocketState.Authenticated -> MaterialTheme.colorScheme.tertiary
}

@Composable
private fun ChatBubble(
    message: Message,
    modifier: Modifier = Modifier
) {
    val isMine = message.isFromMe
    val bubbleColor = if (isMine) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
    val contentColor = if (isMine) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
    val alignment = if (isMine) Alignment.CenterEnd else Alignment.CenterStart

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = alignment
    ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(bubbleColor)
                .padding(12.dp)
        ) {
            if (message.type == "image") {
                AsyncImage(
                    model = imageUrl(message.text),
                    contentDescription = null,
                    modifier = Modifier.size(200.dp),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text(
                    text = message.text,
                    color = contentColor,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            // статус доставки — только под своими сообщениями
            if (isMine) {
                MessageStatusRow(status = message.status)
            }
        }
    }
}

@Composable
private fun MessageStatusRow(status: MessageStatus) {
    val (icon, label, tint) = when (status) {
        MessageStatus.SENDING -> Triple(Icons.Default.Schedule, "отправляется…", Color.Unspecified)
        MessageStatus.SENT -> Triple(Icons.Default.Done, "отправлено", Color.Unspecified)
        MessageStatus.DELIVERED, MessageStatus.READ -> Triple(Icons.Default.DoneAll, "доставлено", Color.Unspecified)
        MessageStatus.FAILED -> Triple(Icons.Default.ErrorOutline, "не доставлено", MaterialTheme.colorScheme.error)
    }
    Row(
        modifier = Modifier.padding(top = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
        )
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (tint == Color.Unspecified) {
                MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
            } else {
                tint
            },
            modifier = Modifier
                .padding(start = 4.dp)
                .size(14.dp)
        )
    }
}

private const val MAX_IMAGE_BYTES = 10 * 1024 * 1024 // 10 MB
