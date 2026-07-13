package com.example.messageapp.ui.screen.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.messageapp.R
import com.example.messageapp.domain.model.User
import com.example.messageapp.ui.components.EmptyState
import com.example.messageapp.ui.screen.search.SearchViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    userName: String,
    name: String,
    onNavigateToChatList: (String, String) -> Unit,
    onNavigateToNews: (String, String) -> Unit,
    onNavigateToAccount: () -> Unit,
    onNavigateToChat: (String, String) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val foundUsers by viewModel.foundUser.collectAsStateWithLifecycle()
    val pendingRequests by viewModel.pendingRequests.collectAsStateWithLifecycle()
    val friendRequestResult by viewModel.friendRequestResult.collectAsStateWithLifecycle()
    val messageNotification by viewModel.messageNotification.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    var query by remember { mutableStateOf("") }
    var pendingDialogUsername by remember { mutableStateOf<String?>(null) }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    DisposableEffect(Unit) {
        viewModel.saveUserName(userName)
        viewModel.startPolling(userName)
        onDispose {
            viewModel.stopPolling()
        }
    }

    LaunchedEffect(friendRequestResult) {
        friendRequestResult?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.resetFriendRequestResult()
        }
    }

    LaunchedEffect(messageNotification) {
        if (messageNotification.isNotBlank()) {
            val sender = when {
                messageNotification.startsWith("Заявка от ") -> {
                    messageNotification.removePrefix("Заявка от ").trim()
                }
                messageNotification.contains(":") -> messageNotification.split(":").firstOrNull()?.trim()
                else -> messageNotification.trim()
            }
            if (!sender.isNullOrBlank()) {
                pendingDialogUsername = sender
            }
        }
    }

    pendingDialogUsername?.let { sender ->
        AlertDialog(
            onDismissRequest = { pendingDialogUsername = null },
            title = { Text(stringResource(id = R.string.titleNotification)) },
            text = { Text("Пользователь $sender хочет добавить вас в друзья") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.acceptFriend(sender, userName)
                        pendingDialogUsername = null
                    }
                ) {
                    Text("Принять")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        viewModel.rejectFriend(sender, userName)
                        pendingDialogUsername = null
                    }
                ) {
                    Text("Отклонить")
                }
            }
        )
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.search_title)) },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { newQuery ->
                    query = newQuery
                    if (newQuery.isBlank()) {
                        viewModel.clearSearchResults()
                    } else {
                        viewModel.findUserByStr(newQuery, userName)
                    }
                },
                label = { Text(stringResource(id = R.string.search_hint)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                shape = RoundedCornerShape(12.dp)
            )

            if (foundUsers.isEmpty() && query.isBlank()) {
                EmptyState(
                    title = stringResource(id = R.string.search_empty_title),
                    description = stringResource(id = R.string.search_empty_desc)
                )
            } else if (foundUsers.isEmpty()) {
                EmptyState(
                    title = stringResource(id = R.string.search_empty_title),
                    description = stringResource(id = R.string.search_empty_desc)
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(vertical = 8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(
                        items = foundUsers,
                        key = { it.userName }
                    ) { user ->
                        UserListItem(
                            user = user,
                            isPending = pendingRequests.contains(user.userName),
                            onAddFriend = {
                                viewModel.sendFriendRequest(userName, user.userName)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun UserListItem(
    user: User,
    isPending: Boolean,
    onAddFriend: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = user.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = user.userName,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp)
            )
            Button(
                onClick = onAddFriend,
                enabled = !isPending,
                modifier = Modifier.padding(top = 12.dp)
            ) {
                Text(
                    text = stringResource(
                        id = if (isPending) R.string.btn_request_sent else R.string.btn_add_friend
                    )
                )
            }
        }
    }
}
