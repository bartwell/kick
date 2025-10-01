package ru.bartwell.kick.module.explorer.feature.list.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import ru.bartwell.kick.core.data.platformContext
import ru.bartwell.kick.core.presentation.ErrorAlert
import ru.bartwell.kick.module.explorer.feature.list.util.FileSystemUtils
import ru.bartwell.kick.module.explorer.feature.list.util.KnownFolder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FileExplorerContent(
    component: FileExplorerComponent,
    modifier: Modifier = Modifier
) {
    val state by component.model.subscribeAsState()
    val context = platformContext()

    var launched by rememberSaveable { mutableStateOf(false) }
    if (!launched) {
        LaunchedEffect(Unit) {
            component.init(context)
            launched = true
        }
    }

    Column(modifier = modifier) {
        TopAppBar(
            title = {
                Text(text = state.folderName, maxLines = 1)
            },
            navigationIcon = {
                IconButton(onClick = { component.onBackClick() }) {
                    Icon(imageVector = Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                }
            }
        )
        KnownFoldersRow(component = component)
        Spacer(modifier = Modifier.height(16.dp))
        EntriesList(component = component, state = state)
        Spacer(modifier = Modifier.height(8.dp))

        val scrollState = rememberScrollState()
        LaunchedEffect(state.currentPath) {
            scrollState.scrollTo(scrollState.maxValue)
        }
        Surface(
            tonalElevation = 2.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .horizontalScroll(scrollState)
                    .padding(12.dp)
            ) {
                Text(
                    text = state.currentPath,
                    maxLines = 1,
                    overflow = TextOverflow.Visible,
                )
            }
        }

        OptionsSheet(
            selectedFileName = state.selectedFileName,
            onFileActionDismiss = component::onFileActionDismiss,
            onDownloadClick = { component.onDownloadClick(context) },
            onViewAsTextClick = component::onViewAsTextClick,
            onDeleteClick = component::onDeleteClick,
        )

        state.exportedFilePath?.let { exportedFilePath ->
            SuccessAlert(path = exportedFilePath, onExportAlertDismiss = component::onExportAlertDismiss)
        }
        state.fileToDelete?.let { fileName ->
            DeleteAlert(
                fileName = fileName,
                onConfirm = component::onDeleteConfirm,
                onDismiss = component::onDeleteDismiss
            )
        }
        state.error?.let { error ->
            ErrorAlert(message = error, onDismiss = component::onErrorAlertDismiss)
        }
    }
}

@Composable
private fun KnownFoldersRow(component: FileExplorerComponent) {
    val context = platformContext()
    var knownFolders by remember { mutableStateOf(emptyList<KnownFolder>()) }
    LaunchedEffect(Unit) {
        knownFolders = FileSystemUtils.getKnownFolders(context)
    }

    if (knownFolders.size > 1) {
        LazyRow(modifier = Modifier.padding(horizontal = 16.dp)) {
            items(knownFolders) { folder ->
                AssistChip(
                    onClick = { component.onKnownFolderClick(folder.path) },
                    label = { Text(folder.name) }
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}

@Composable
private fun ColumnScope.EntriesList(
    component: FileExplorerComponent,
    state: FileExplorerState
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth()
            .weight(1f)
    ) {
        if (state.canGoUp) {
            item {
                ListItem(
                    modifier = Modifier.clickable(onClick = component::onUpClick),
                    headlineContent = { Text(text = "..") },
                    leadingContent = {
                        Icon(imageVector = Icons.Default.ArrowUpward, contentDescription = null)
                    },
                )
            }
        }
        items(state.entries) { entry ->
            ListItem(
                modifier = Modifier.clickable {
                    if (entry.isDirectory) {
                        component.onDirectoryClick(entry.name)
                    } else {
                        component.onFileClick(entry.name)
                    }
                },
                headlineContent = { Text(entry.name) },
                leadingContent = {
                    Icon(
                        if (entry.isDirectory) {
                            Icons.Default.Folder
                        } else {
                            Icons.Default.Description
                        },
                        contentDescription = null
                    )
                },
                supportingContent = entry.size?.let { { Text("$it bytes") } }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OptionsSheet(
    selectedFileName: String?,
    onFileActionDismiss: () -> Unit,
    onDownloadClick: () -> Unit,
    onViewAsTextClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    if (selectedFileName != null) {
        ModalBottomSheet(
            onDismissRequest = onFileActionDismiss,
            sheetState = sheetState
        ) {
            ListItem(
                modifier = Modifier.clickable(onClick = onDownloadClick),
                headlineContent = { Text(text = "Download") }
            )
            ListItem(
                modifier = Modifier.clickable(onClick = onViewAsTextClick),
                headlineContent = { Text(text = "View as text") }
            )
            ListItem(
                modifier = Modifier.clickable(onClick = onDeleteClick),
                headlineContent = { Text(text = "Delete") }
            )
        }
    }
}

@Composable
private fun SuccessAlert(
    path: String,
    onExportAlertDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onExportAlertDismiss,
        confirmButton = {
            TextButton(onClick = onExportAlertDismiss) {
                Text("OK")
            }
        },
        title = { Text(text = "File exported") },
        text = { Text(text = path) }
    )
}

@Composable
private fun DeleteAlert(
    fileName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        title = { Text(text = "Delete file") },
        text = { Text(text = "Delete $fileName?") }
    )
}
