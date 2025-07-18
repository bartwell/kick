package ru.bartwell.kick.runtime.feature.list.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import ru.bartwell.kick.runtime.feature.list.data.ModuleInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ModulesListContent(
    component: ModulesListComponent,
    modifier: Modifier = Modifier,
) {
    val state by component.model.subscribeAsState()
    val screenCloser = screenCloser()

    Column(modifier = modifier) {
        TopAppBar(
            title = { Text("Modules") },
            navigationIcon = {
                IconButton(onClick = screenCloser) {
                    Icon(imageVector = Icons.Outlined.Close, contentDescription = "Close")
                }
            },
            actions = {
                if (!state.isAllModulesEnabled) {
                    val (icon, contentDescription) = if (state.showAll) {
                        Icons.Default.ExpandLess to "Show enabled modules"
                    } else {
                        Icons.Default.ExpandMore to "Show all modules"
                    }
                    IconButton(onClick = component::onShowAllClicked) {
                        Icon(imageVector = icon, contentDescription = contentDescription)
                    }
                }
            }
        )
        LazyColumn(
            state = rememberLazyListState(),
            modifier = Modifier.fillMaxSize(),
        ) {
            items(state.modulesToShow) { module ->
                Item(
                    module = module,
                    onClick = { component.onListItemClicked(module.moduleDescription) },
                )
            }
        }
    }
}

@Composable
private fun Item(module: ModuleInfo, onClick: () -> Unit) {
    val backgroundColor = if (module.isEnabled) {
        ListItemDefaults.containerColor
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    ListItem(
        modifier = Modifier.clickable(onClick = onClick),
        headlineContent = { Text(module.moduleDescription.title) },
        colors = ListItemDefaults.colors(containerColor = backgroundColor),
    )
}
