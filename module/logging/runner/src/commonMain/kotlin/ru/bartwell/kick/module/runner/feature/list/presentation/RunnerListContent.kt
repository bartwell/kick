package ru.bartwell.kick.module.runner.feature.list.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import ru.bartwell.kick.core.presentation.BackOrCloseButton
import ru.bartwell.kick.core.presentation.ErrorBox

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun RunnerListContent(
    component: RunnerListComponent,
    modifier: Modifier = Modifier,
) {
    val state by component.model.subscribeAsState()

    Column(modifier = modifier) {
        TopAppBar(
            title = { Text("Runner") },
            navigationIcon = {
                BackOrCloseButton(onBack = component::onBackPressed)
            },
        )

        ErrorBox(error = state.error, modifier = Modifier.fillMaxSize()) {
            if (state.calls.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No calls added yet")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = rememberLazyListState(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                ) {
                    items(state.calls) { item ->
                        RunnerListItemRow(
                            item = item,
                            onRunClick = component::onCallClick,
                            isRunning = state.runningCallId == item.id,
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun RunnerListItemRow(
    item: RunnerListItem,
    onRunClick: (String) -> Unit,
    isRunning: Boolean,
) {
    ListItem(
        modifier = Modifier.fillMaxWidth(),
        headlineContent = { Text(item.title) },
        supportingContent = item.description?.let { desc -> { Text(desc) } },
        trailingContent = {
            if (isRunning) {
                CircularProgressIndicator(modifier = Modifier.height(24.dp))
            } else {
                Button(onClick = { onRunClick(item.id) }) {
                    Text("Run")
                }
            }
        }
    )
}
