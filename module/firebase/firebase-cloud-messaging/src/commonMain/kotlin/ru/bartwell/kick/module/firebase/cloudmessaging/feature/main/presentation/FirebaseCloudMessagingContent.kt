package ru.bartwell.kick.module.firebase.cloudmessaging.feature.main.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import ru.bartwell.kick.core.data.platformContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FirebaseCloudMessagingContent(
    component: FirebaseCloudMessagingComponent,
    modifier: Modifier = Modifier,
) {
    val state by component.state.subscribeAsState()
    val context = platformContext()

    var launched by rememberSaveable { mutableStateOf(false) }
    if (!launched) {
        LaunchedEffect(Unit) {
            component.init(context)
            launched = true
        }
    }

    var isMenuExpanded by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    Column(modifier = modifier) {
        TopAppBar(
            title = { Text("Firebase Cloud Messaging") },
            navigationIcon = {
                IconButton(onClick = component::onBackPressed, modifier = Modifier.testTag("back")) {
                    Icon(imageVector = Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                IconButton(onClick = { isMenuExpanded = true }, modifier = Modifier.testTag("menu_button")) {
                    Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "Menu")
                }
                DropdownMenu(
                    expanded = isMenuExpanded,
                    onDismissRequest = { isMenuExpanded = false },
                ) {
                    DropdownMenuItem(
                        modifier = Modifier.testTag("menu_history"),
                        text = { Text("History") },
                        onClick = {
                            isMenuExpanded = false
                            component.onHistoryClick()
                        },
                        leadingIcon = { Icon(imageVector = Icons.Outlined.History, contentDescription = null) },
                    )
                }
            },
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            state = listState,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                AvailabilitySection(state)
            }

            item {
                TokenSection(
                    state = state,
                    onRefresh = { component.refreshToken(context, forceRefresh = true) },
                    onCopy = { component.copyToken(context) },
                )
            }

            item {
                FirebaseIdSection(
                    state = state,
                    onRefresh = { component.refreshFirebaseId(context) },
                    onCopy = { component.copyFirebaseId(context) },
                )
            }
        }
    }
}

@Composable
private fun AvailabilitySection(state: FirebaseCloudMessagingState) {
    val text = state.availabilityMessage
    if (text != null) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = text,
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
            )
        }
    }
}

@Composable
private fun TokenSection(
    state: FirebaseCloudMessagingState,
    onRefresh: () -> Unit,
    onCopy: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text("Registration token", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                IconButton(onClick = onRefresh, enabled = !state.isTokenLoading) {
                    Icon(Icons.Outlined.Refresh, contentDescription = "Refresh token")
                }
                IconButton(onClick = onCopy, enabled = state.token != null) {
                    Icon(Icons.Outlined.ContentCopy, contentDescription = "Copy token")
                }
            }
            if (state.isTokenLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
            state.token?.let { token ->
                SelectionContainer {
                    Text(token, style = MaterialTheme.typography.bodyMedium)
                }
            }
            state.tokenError?.let { error ->
                Text(text = error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun FirebaseIdSection(
    state: FirebaseCloudMessagingState,
    onRefresh: () -> Unit,
    onCopy: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text(
                    "Firebase installation id",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onRefresh, enabled = !state.isFirebaseIdLoading) {
                    Icon(Icons.Outlined.Refresh, contentDescription = "Refresh id")
                }
                IconButton(onClick = onCopy, enabled = state.firebaseId != null) {
                    Icon(Icons.Outlined.ContentCopy, contentDescription = "Copy id")
                }
            }
            if (state.isFirebaseIdLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
            state.firebaseId?.let { id ->
                SelectionContainer {
                    Text(id, style = MaterialTheme.typography.bodyMedium)
                }
            }
            state.firebaseIdError?.let { error ->
                Text(text = error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
