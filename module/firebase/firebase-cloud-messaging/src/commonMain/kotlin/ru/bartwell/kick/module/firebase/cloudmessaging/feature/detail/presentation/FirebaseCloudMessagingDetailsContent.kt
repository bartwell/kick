package ru.bartwell.kick.module.firebase.cloudmessaging.feature.detail.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import ru.bartwell.kick.core.util.DateUtils
import ru.bartwell.kick.module.firebase.cloudmessaging.core.data.FirebaseMessage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FirebaseCloudMessagingDetailsContent(
    component: FirebaseCloudMessagingDetailsComponent,
    modifier: Modifier = Modifier,
) {
    val state by component.state.subscribeAsState()
    val clipboard = LocalClipboardManager.current

    Column(modifier = modifier) {
        TopAppBar(
            title = { Text("Push details") },
            navigationIcon = {
                IconButton(onClick = component::onBackPressed, modifier = Modifier.testTag("back")) {
                    Icon(imageVector = Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                IconButton(
                    onClick = {
                        clipboard.setText(AnnotatedString(state.message.toClipboardText()))
                    },
                ) {
                    Icon(imageVector = Icons.Outlined.ContentCopy, contentDescription = "Copy all")
                }
            },
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                MessageDetailsCard(state.message)
            }
        }
    }
}

@Composable
private fun MessageDetailsCard(message: FirebaseMessage) {
    var rawExpanded by rememberSaveable { mutableStateOf(false) }
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        message.title?.let { title ->
            Text(title, style = MaterialTheme.typography.bodyMedium)
        }
        message.body?.let { body ->
            Text(body, style = MaterialTheme.typography.bodyMedium)
        }
        if (message.data.isNotEmpty()) {
            Text("Data:", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
            SelectionContainer {
                Text(message.data.entries.joinToString(separator = "\n") { "${it.key}: ${it.value}" })
            }
        }
        HorizontalDivider()
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
        InfoRow(label = "Sent", value = message.sentTimeMillis?.let { DateUtils.formatLogTime(it) })
        InfoRow(label = "Received", value = DateUtils.formatLogTime(message.receivedAtMillis))
        if (message.raw.isNotEmpty()) {
            HorizontalDivider()
            TextButton(onClick = { rawExpanded = !rawExpanded }) {
                Text(
                    text = if (rawExpanded) "Hide raw payload" else "Show raw payload",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            if (rawExpanded) {
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
            Text(label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
            Text(value, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

private fun FirebaseMessage.toClipboardText(): String {
    val lines = buildList {
        addIfNotBlank("Title", title)
        addIfNotBlank("Body", body)
        addMapSection("Data", data)
        addIfNotBlank("From", from)
        addIfNotBlank("To", to)
        addIfNotBlank("Message id", messageId)
        addIfNotBlank("Collapse key", collapseKey)
        addIfNotBlank("Channel", channelId)
        addIfNotBlank("Category", category)
        addIfNotBlank("Thread", threadId)
        addIfNotBlank("Badge", badge)
        addIfNotBlank("Sound", sound)
        addIfNotBlank("Tag", tag)
        addIfNotBlank("Image", imageUrl)
        addIfNotBlank("Priority", priority)
        ttlSeconds?.let { add("TTL: $it s") }
        sentTimeMillis?.let { add("Sent: ${DateUtils.formatLogTime(it)}") }
        add("Received: ${DateUtils.formatLogTime(receivedAtMillis)}")
        addMapSection("Raw payload", raw)
    }
    return lines.joinToString("\n")
}

private fun MutableList<String>.addIfNotBlank(label: String, value: String?) {
    if (!value.isNullOrBlank()) {
        add("$label: $value")
    }
}

private fun MutableList<String>.addMapSection(label: String, map: Map<String, String>) {
    if (map.isNotEmpty()) {
        add("$label:")
        map.entries.forEach { (key, value) -> add("$key: $value") }
    }
}
