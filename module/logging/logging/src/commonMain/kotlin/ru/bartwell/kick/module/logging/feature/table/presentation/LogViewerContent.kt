package ru.bartwell.kick.module.logging.feature.table.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.FilterListOff
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import ru.bartwell.kick.core.data.Platform
import ru.bartwell.kick.core.data.platformContext
import ru.bartwell.kick.core.presentation.ErrorBox
import ru.bartwell.kick.core.util.PlatformUtils
import ru.bartwell.kick.module.logging.core.data.LogLevel
import ru.bartwell.kick.module.logging.core.persist.LogEntity
import ru.bartwell.kick.module.logging.feature.table.extension.toLogString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun LogViewerContent(
    component: LogViewerComponent,
    modifier: Modifier = Modifier,
) {
    val state by component.model.subscribeAsState()
    val context = platformContext()

    Column(modifier = modifier) {
        TopAppBar(
            title = { Text("Logging") },
            navigationIcon = {
                IconButton(onClick = component::onBackPressed) {
                    Icon(imageVector = Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                IconButton(onClick = component::onFilterClick) {
                    val (icon, description) = if (state.isFilterActive) {
                        Icons.Default.FilterListOff to "Disable filter"
                    } else {
                        Icons.Default.FilterList to "Filter logs"
                    }
                    Icon(imageVector = icon, contentDescription = description)
                }
                IconButton(onClick = component::onClearAllClick) {
                    Icon(imageVector = Icons.Default.ClearAll, contentDescription = "Clear all")
                }
                IconButton(onClick = { component.onShareClick(context) }) {
                    val (icon, contentDescription) = if (PlatformUtils.getPlatform() == Platform.IOS) {
                        Icons.Default.ContentCopy to "Copy logs"
                    } else {
                        Icons.Default.Share to "Share logs"
                    }
                    Icon(imageVector = icon, contentDescription = contentDescription)
                }
            }
        )
        if (state.isFilterDialogVisible) {
            FilterDialog(component = component, state = state)
        }
        ErrorBox(modifier = Modifier.fillMaxSize(), error = state.error) {
            LazyColumn(
                state = rememberLazyListState(),
                modifier = Modifier.fillMaxSize(),
            ) {
                items(state.log) { item ->
                    Item(item)
                }
            }
        }
    }
}

@Composable
private fun FilterDialog(component: LogViewerComponent, state: LogViewerState) {
    AlertDialog(
        onDismissRequest = { component.onFilterDialogDismiss() },
        title = { Text("Filter logs") },
        text = {
            OutlinedTextField(
                value = state.filterQuery,
                onValueChange = { component.onFilterTextChange(it) },
                label = { Text("Message contains...") }
            )
        },
        confirmButton = {
            TextButton(
                enabled = state.filterQuery.isNotBlank(),
                onClick = component::onFilterApply,
            ) {
                Text("Filter")
            }
        },
        dismissButton = {
            TextButton(onClick = component::onFilterDialogDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun Item(item: LogEntity) {
    Text(
        text = item.toLogString(),
        style = MaterialTheme.typography.bodySmall,
        color = item.level.color,
    )
}

@Suppress("MagicNumber")
private val LogLevel.color: Color
    @Composable
    get() = when (this) {
        LogLevel.VERBOSE -> MaterialTheme.colorScheme.onBackground
        LogLevel.DEBUG -> Color(0xFF305D78)
        LogLevel.INFO -> Color(0xFF6A8759)
        LogLevel.WARNING -> Color(0xFFBBB529)
        LogLevel.ERROR -> Color(0xFFCF5B56)
        LogLevel.ASSERT -> Color(0xFF8B3C3C)
    }
