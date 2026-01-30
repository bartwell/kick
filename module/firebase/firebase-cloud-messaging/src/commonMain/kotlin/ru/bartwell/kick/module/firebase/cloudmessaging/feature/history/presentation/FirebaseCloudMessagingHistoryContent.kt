package ru.bartwell.kick.module.firebase.cloudmessaging.feature.history.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import ru.bartwell.kick.core.data.Platform
import ru.bartwell.kick.core.util.DateUtils
import ru.bartwell.kick.core.util.PlatformUtils
import ru.bartwell.kick.module.firebase.cloudmessaging.core.data.FirebaseMessage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FirebaseCloudMessagingHistoryContent(
    component: FirebaseCloudMessagingHistoryComponent,
    modifier: Modifier = Modifier,
) {
    val state by component.state.subscribeAsState()
    val platform = PlatformUtils.getPlatform()

    Column(modifier = modifier) {
        TopAppBar(
            title = { Text("Push history") },
            navigationIcon = {
                IconButton(onClick = component::onBackPressed, modifier = Modifier.testTag("back")) {
                    Icon(imageVector = Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                if (state.messages.isNotEmpty()) {
                    IconButton(onClick = component::onClearMessages, modifier = Modifier.testTag("clear_all")) {
                        Icon(imageVector = Icons.Filled.ClearAll, contentDescription = "Clear")
                    }
                }
            },
        )

        if (state.messages.isEmpty()) {
            EmptyStateCard(text = buildHistoryEmptyMessage(platform))
        } else {
            val listState = rememberLazyListState()
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = listState,
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(state.messages, key = { it.receivedAtMillis to (it.messageId ?: it.hashCode()) }) { message ->
                    MessageCard(message, onClick = { component.onMessageClick(message) })
                }
            }
        }
    }
}

@Composable
private fun EmptyStateCard(text: String) {
    Text(
        text = text,
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
private fun MessageCard(message: FirebaseMessage, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            message.title?.let { title ->
                Text(title, style = MaterialTheme.typography.labelLarge)
            }
            message.body?.let { body ->
                Text(body, style = MaterialTheme.typography.labelLarge)
            }
            InfoRow(label = "Received at", value = DateUtils.formatLogTime(message.receivedAtMillis))
            InfoRow(label = "From", value = message.from)
            InfoRow(label = "Message id", value = message.messageId)
            if (message.data.isNotEmpty()) {
                Text(
                    text = "Data entries: ${message.data.size}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String?) {
    if (!value.isNullOrBlank()) {
        Text(
            text = "$label: $value",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

private fun buildHistoryEmptyMessage(platform: Platform): String = when (platform) {
    Platform.ANDROID ->
        "No push history yet. If you expect pushes, make sure your FirebaseMessagingService calls " +
            "Kick.firebaseCloudMessaging.handleFcm(message)."
    Platform.IOS ->
        "No push history yet. If you expect pushes, make sure your AppDelegate calls " +
            "KickCompanion().firebaseCloudMessaging.handleApnsPayload(userInfo: userInfo)."
    else -> "No push history yet."
}
