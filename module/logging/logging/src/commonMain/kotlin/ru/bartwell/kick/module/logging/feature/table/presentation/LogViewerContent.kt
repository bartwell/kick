package ru.bartwell.kick.module.logging.feature.table.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileDownloadOff
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.FilterListOff
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import ru.bartwell.kick.core.data.platformContext
import ru.bartwell.kick.core.presentation.BackOrCloseButton
import ru.bartwell.kick.core.presentation.ErrorBox
import ru.bartwell.kick.module.logging.core.data.LogLevel
import ru.bartwell.kick.module.logging.core.persist.LogEntity
import ru.bartwell.kick.module.logging.feature.table.extension.toLogString
import ru.bartwell.kick.module.logging.feature.table.util.LaunchUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun LogViewerContent(
    component: LogViewerComponent,
    modifier: Modifier = Modifier,
) {
    val state by component.model.subscribeAsState()
    val context = platformContext()
    val listState = rememberLazyListState()
    var isMenuExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(state.isAutoScrollEnabled, state.log.lastOrNull()?.id) {
        if (state.isAutoScrollEnabled && state.log.isNotEmpty()) {
            listState.animateScrollToItem(state.log.lastIndex)
        }
    }

    Column(modifier = modifier) {
        TopAppBar(
            title = { Text("Logging") },
            navigationIcon = {
                BackOrCloseButton(onBack = component::onBackPressed)
            },
            actions = {
                IconButton(onClick = component::onFilterClick, modifier = Modifier.testTag("filter_toggle")) {
                    val (icon, description) = if (state.isFilterActive) {
                        Icons.Default.FilterListOff to "Disable filter"
                    } else {
                        Icons.Default.FilterList to "Filter logs"
                    }
                    Icon(imageVector = icon, contentDescription = description)
                }
                IconButton(
                    onClick = component::onAutoScrollToggleClick,
                    modifier = Modifier.testTag("auto_scroll_toggle")
                ) {
                    val (icon, description) = if (state.isAutoScrollEnabled) {
                        Icons.Filled.FileDownload to "Disable auto-scroll"
                    } else {
                        Icons.Filled.FileDownloadOff to "Enable auto-scroll"
                    }
                    Icon(imageVector = icon, contentDescription = description)
                }
                IconButton(onClick = component::onClearAllClick, modifier = Modifier.testTag("clear_all")) {
                    Icon(imageVector = Icons.Default.ClearAll, contentDescription = "Clear all")
                }
                IconButton(onClick = { isMenuExpanded = true }, modifier = Modifier.testTag("overflow_menu")) {
                    Icon(imageVector = Icons.Default.MoreVert, contentDescription = "Menu")
                }
                DropdownMenu(expanded = isMenuExpanded, onDismissRequest = { isMenuExpanded = false }) {
                    if (LaunchUtils.canCopyLogs()) {
                        DropdownMenuItem(
                            text = { Text("Copy") },
                            onClick = {
                                isMenuExpanded = false
                                component.onCopyClick(context)
                            },
                            modifier = Modifier.testTag("copy_logs")
                        )
                    }
                    if (LaunchUtils.canSaveLogsToFile()) {
                        DropdownMenuItem(
                            text = { Text("Save to file") },
                            onClick = {
                                isMenuExpanded = false
                                component.onSaveToFileClick(context)
                            },
                            modifier = Modifier.testTag("save_to_file")
                        )
                    }
                    if (LaunchUtils.canShareLogsAsText()) {
                        DropdownMenuItem(
                            text = { Text("Share as text") },
                            onClick = {
                                isMenuExpanded = false
                                component.onShareAsTextClick(context)
                            },
                            modifier = Modifier.testTag("share_as_text")
                        )
                    }
                    if (LaunchUtils.canShareLogsAsFile()) {
                        DropdownMenuItem(
                            text = { Text("Share as file") },
                            onClick = {
                                isMenuExpanded = false
                                component.onShareAsFileClick(context)
                            },
                            modifier = Modifier.testTag("share_as_file")
                        )
                    }
                }
            }
        )
        if (state.isFilterDialogVisible) {
            FilterDialog(component = component, state = state)
        }
        if (state.labels.isNotEmpty()) {
            LabelsBar(component = component, state = state)
        }
        ErrorBox(modifier = Modifier.fillMaxSize(), error = state.error) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize().testTag("log_list"),
            ) {
                items(state.log) { item ->
                    Item(item)
                }
            }
        }
    }
}

@Composable
private fun LabelsBar(component: LogViewerComponent, state: LogViewerState) {
    LazyRow(modifier = Modifier.testTag("label_chips")) {
        items(state.labels) { label ->
            FilterChip(
                selected = state.selectedLabels.contains(label),
                onClick = { component.onLabelClick(label) },
                label = { Text(label) },
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
            )
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
        modifier = Modifier.testTag("log_item"),
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
