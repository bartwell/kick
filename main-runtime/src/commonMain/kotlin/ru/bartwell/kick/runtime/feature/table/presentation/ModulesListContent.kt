package ru.bartwell.kick.runtime.feature.table.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState

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
        )
        LazyColumn(
            state = rememberLazyListState(),
            modifier = Modifier.fillMaxSize(),
        ) {
            items(state.modules) { module ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { component.onListItemClicked(module) }
                        .padding(16.dp)
                ) {
                    Text(module.title)
                }
            }
        }
    }
}
