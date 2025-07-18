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
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import ru.bartwell.kick.core.data.platformContext
import ru.bartwell.kick.module.explorer.feature.list.util.FileSystemUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FileExplorerContent(
    component: FileExplorerComponent,
    modifier: Modifier = Modifier
) {
    val state by component.model.subscribeAsState()
    val context = platformContext()
    LaunchedEffect(Unit) {
        component.init(context)
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
    }
}

@Composable
private fun KnownFoldersRow(component: FileExplorerComponent) {
    val context = platformContext()
    val knownFolders = remember { FileSystemUtils.getKnownFolders(context) }

    if (knownFolders.isNotEmpty()) {
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
