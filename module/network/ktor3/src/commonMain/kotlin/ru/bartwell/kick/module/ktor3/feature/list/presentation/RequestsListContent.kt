package ru.bartwell.kick.module.ktor3.feature.list.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import ru.bartwell.kick.core.persist.RequestEntity
import ru.bartwell.kick.core.presentation.ErrorBox
import ru.bartwell.kick.module.ktor3.feature.detail.extension.formatDuration
import ru.bartwell.kick.module.ktor3.feature.detail.extension.formatFileSize
import ru.bartwell.kick.module.ktor3.feature.detail.extension.formatTimestamp
import ru.bartwell.kick.module.ktor3.feature.detail.extension.getDomain
import ru.bartwell.kick.module.ktor3.feature.detail.extension.getPath

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun RequestsListContent(
    component: RequestsListComponent,
    modifier: Modifier = Modifier,
) {
    val state by component.model.subscribeAsState()

    Column(modifier = modifier) {
        TopAppBar(
            title = { Text("Network Requests") },
            navigationIcon = {
                IconButton(onClick = component::onBackPressed) {
                    Icon(imageVector = Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                IconButton(onClick = component::onSearchClick) {
                    val (icon, contentDescription) = if (state.searchQuery.isBlank()) {
                        Icons.Default.Search to "Search requests"
                    } else {
                        Icons.Default.SearchOff to "Cancel search"
                    }
                    Icon(imageVector = icon, contentDescription = contentDescription)
                }
                IconButton(onClick = component::onClearAllClick) {
                    Icon(imageVector = Icons.Default.ClearAll, contentDescription = "Clear all")
                }
            }
        )
        if (state.isSearchDialogVisible) {
            AlertDialog(
                onDismissRequest = component::onSearchDialogDismiss,
                title = { Text("Search") },
                text = {
                    OutlinedTextField(
                        value = state.searchQuery,
                        onValueChange = component::onSearchTextChange,
                        label = { Text("Query") }
                    )
                },
                confirmButton = {
                    TextButton(
                        enabled = state.searchQuery.isNotBlank(),
                        onClick = component::onSearchApply
                    ) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = component::onSearchDialogDismiss) {
                        Text("Cancel")
                    }
                }
            )
        }
        ErrorBox(modifier = Modifier.fillMaxSize(), error = state.error) {
            LazyColumn(
                state = rememberLazyListState(),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.requests) { item ->
                    RequestItem(
                        item = item,
                        onClick = { component.onItemClick(item.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun RequestItem(
    item: RequestEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                val (statusColor, statusText) = item.statusCode.toStatusInfo(item.error)
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.labelLarge,
                    color = statusColor,
                )
                Text(text = item.method.name, style = MaterialTheme.typography.labelLarge)
                Text(text = item.url.getPath(), style = MaterialTheme.typography.labelLarge)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = item.url.getDomain(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Icon(
                    modifier = Modifier.size(16.dp),
                    imageVector = if (item.isSecure) Icons.Default.Lock else Icons.Default.LockOpen,
                    contentDescription = if (item.isSecure) "Secure connection" else "Insecure connection"
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.formatTimestamp(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.formatDuration(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    item.responseSizeBytes?.let { size ->
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "• ${size.formatFileSize()}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Suppress("MagicNumber")
@Composable
private fun Int?.toStatusInfo(error: String?) = when {
    error != null -> Color(0xFFF44336) to "ERROR"
    this == null -> Color(0xFF757575) to "UNKNOWN"
    this in 200..299 -> Color(0xFF4CAF50) to toString()
    this in 300..399 -> Color(0xFF2196F3) to toString()
    this in 400..499 -> Color(0xFFFF9800) to toString()
    this in 500..599 -> Color(0xFFF44336) to toString()
    else -> Color(0xFF757575) to toString()
}
