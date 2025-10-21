package ru.bartwell.kick.module.firebase.cloudmessaging.feature.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import ru.bartwell.kick.core.data.platformContext
import ru.bartwell.kick.module.firebase.cloudmessaging.core.data.AndroidNotificationChannelStatus
import ru.bartwell.kick.module.firebase.cloudmessaging.core.data.FirebaseMessage
import ru.bartwell.kick.module.firebase.cloudmessaging.core.data.FirebaseNotificationStatus

@Composable
internal fun FirebaseCloudMessagingContent(
    component: FirebaseCloudMessagingComponent,
    modifier: Modifier = Modifier,
) {
    val state by component.state.subscribeAsState()
    val context = platformContext()
    val listState = rememberLazyListState()

    Column(modifier = modifier) {
        TopAppBar(
            title = { Text("Firebase Cloud Messaging") },
            navigationIcon = {
                IconButton(onClick = component::onBackPressed, modifier = Modifier.testTag("back")) {
                    Icon(imageVector = Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
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
                TokenSection(state, onRefresh = { component.refreshToken(forceRefresh = true) }) {
                    component.copyToken(context)
                }
            }

            item {
                FirebaseIdSection(state, onRefresh = component::refreshFirebaseId) {
                    component.copyFirebaseId(context)
                }
            }

            item {
                StatusSection(state, onRefresh = component::refreshStatus)
            }

            item {
                LocalNotificationSection(state.localNotification,
                    onTitleChange = component::onLocalNotificationTitleChange,
                    onBodyChange = component::onLocalNotificationBodyChange,
                    onDataChange = component::onLocalNotificationDataChange,
                    onChannelChange = component::onLocalNotificationChannelChange,
                    onSend = component::sendLocalNotification,
                    onDismissFeedback = component::onLocalNotificationFeedbackConsumed,
                )
            }

            item {
                MessagesHeader(state.messages, onClear = component::clearMessages)
            }

            items(state.messages, key = { it.receivedAtMillis to (it.messageId ?: it.hashCode()) }) { message ->
                MessageCard(message)
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
                Text("Firebase installation id", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
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

@Composable
private fun StatusSection(
    state: FirebaseCloudMessagingState,
    onRefresh: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text("Notification status", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                IconButton(onClick = onRefresh, enabled = !state.isStatusLoading) {
                    Icon(Icons.Outlined.Refresh, contentDescription = "Refresh status")
                }
            }
            if (state.isStatusLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
            state.status?.let { status ->
                StatusDetails(status)
            }
            state.statusError?.let { error ->
                Text(text = error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun StatusDetails(status: FirebaseNotificationStatus) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        status.iosPermission?.let { permission ->
            StatusRow(title = "iOS permission", value = permission.description)
        }
        status.androidChannel?.let { channel ->
            StatusRow(title = "Android channel", value = buildAndroidChannelSummary(channel))
        }
        status.isGooglePlayServicesAvailable?.let { available ->
            StatusRow(title = "Google Play Services", value = if (available) "Available" else "Unavailable")
        }
    }
}

@Composable
private fun StatusRow(title: String, value: String) {
    Column {
        Text(title, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}

private fun buildAndroidChannelSummary(channel: AndroidNotificationChannelStatus): String {
    val parts = buildList {
        add(channel.id)
        channel.name?.let { add("Name: $it") }
        channel.importance?.let { add("Importance: $it") }
        channel.isEnabled?.let { add(if (it) "Enabled" else "Disabled") }
        channel.isAppNotificationsEnabled?.let { add(if (it) "Notifications allowed" else "Notifications blocked") }
        channel.description?.let { add("Description: $it") }
    }
    return parts.joinToString(separator = "\n")
}

@Composable
private fun LocalNotificationSection(
    state: LocalNotificationState,
    onTitleChange: (String) -> Unit,
    onBodyChange: (String) -> Unit,
    onDataChange: (String) -> Unit,
    onChannelChange: (String) -> Unit,
    onSend: () -> Unit,
    onDismissFeedback: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Local push emulation", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = state.title,
                onValueChange = onTitleChange,
                label = { Text("Title") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = state.body,
                onValueChange = onBodyChange,
                label = { Text("Body") },
                singleLine = false,
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = state.channelId,
                onValueChange = onChannelChange,
                label = { Text("Channel id (Android)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = state.data,
                onValueChange = onDataChange,
                label = { Text("Data (JSON object)") },
                singleLine = false,
                modifier = Modifier.fillMaxWidth().height(120.dp),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Button(onClick = onSend, enabled = !state.isSending, modifier = Modifier.testTag("send_local_notification")) {
                    Icon(Icons.Outlined.Send, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Send")
                }
                if (state.isSending) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                }
                state.successMessage?.let { message ->
                    TextButton(onClick = onDismissFeedback) {
                        Text(message)
                    }
                }
            }
            state.error?.let { error ->
                Text(text = error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun MessagesHeader(messages: List<FirebaseMessage>, onClear: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text("Received pushes (${messages.size})", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
        if (messages.isNotEmpty()) {
            OutlinedButton(onClick = onClear) {
                Icon(Icons.Outlined.Delete, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Clear")
            }
        }
    }
}

@Composable
private fun MessageCard(message: FirebaseMessage) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            message.title?.let { title ->
                Text(title, style = MaterialTheme.typography.titleMedium)
            }
            message.body?.let { body ->
                Text(body, style = MaterialTheme.typography.bodyMedium)
            }
            if (message.data.isNotEmpty()) {
                Text("Data:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                SelectionContainer {
                    Text(message.data.entries.joinToString(separator = "\n") { "${it.key}: ${it.value}" })
                }
            }
            Divider()
            InfoRow(label = "From", value = message.from)
            InfoRow(label = "To", value = message.to)
            InfoRow(label = "Message id", value = message.messageId)
            InfoRow(label = "Collapse key", value = message.collapseKey)
            InfoRow(label = "Channel", value = message.channelId)
            InfoRow(label = "Category", value = message.category)
            InfoRow(label = "Thread", value = message.threadId)
            InfoRow(label = "Badge", value = message.badge)
            InfoRow(label = "Sound", value = message.sound)
            InfoRow(label = "Tag", value = message.tag)
            InfoRow(label = "Image", value = message.imageUrl)
            InfoRow(label = "Priority", value = message.priority)
            InfoRow(label = "TTL", value = message.ttlSeconds?.let { "$it s" })
            InfoRow(label = "Sent", value = formatTimestamp(message.sentTimeMillis))
            InfoRow(label = "Received", value = formatTimestamp(message.receivedAtMillis))
            if (message.raw.isNotEmpty()) {
                Divider()
                Text("Raw payload:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                SelectionContainer {
                    Text(message.raw.entries.joinToString(separator = "\n") { "${it.key}: ${it.value}" })
                }
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String?) {
    if (!value.isNullOrBlank()) {
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold)
            Text(value, style = MaterialTheme.typography.bodySmall)
        }
    }
}

private fun formatTimestamp(value: Long?): String? {
    if (value == null) return null
    return try {
        val instant = Instant.fromEpochMilliseconds(value)
        val local = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        "%04d-%02d-%02d %02d:%02d:%02d".format(
            local.year,
            local.monthNumber,
            local.dayOfMonth,
            local.hour,
            local.minute,
            local.second,
        )
    } catch (_: Throwable) {
        value.toString()
    }
}
