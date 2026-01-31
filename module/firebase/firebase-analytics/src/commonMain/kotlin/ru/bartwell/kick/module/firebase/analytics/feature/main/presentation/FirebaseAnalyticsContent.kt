package ru.bartwell.kick.module.firebase.analytics.feature.main.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.outlined.List
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import ru.bartwell.kick.core.presentation.ErrorBox
import ru.bartwell.kick.core.util.DateUtils
import ru.bartwell.kick.module.firebase.analytics.core.data.AnalyticsEvent
import ru.bartwell.kick.module.firebase.analytics.core.util.FirebaseFloatingWindowState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FirebaseAnalyticsContent(
    component: FirebaseAnalyticsComponent,
    modifier: Modifier = Modifier,
) {
    val state by component.model.subscribeAsState()
    val floatingVisible by FirebaseFloatingWindowState.visible.collectAsState()
    Column(modifier = modifier) {
        TopAppBar(
            title = { Text("Firebase Analytics") },
            navigationIcon = {
                IconButton(onClick = component::onBackPressed) {
                    Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                IconButton(onClick = component::onClearEvents) {
                    Icon(Icons.Default.ClearAll, contentDescription = "Clear events")
                }
                IconButton(onClick = component::onPropertiesClick) {
                    Icon(Icons.Outlined.List, contentDescription = "Properties")
                }
            }
        )
        FloatingWindowToggle(visible = floatingVisible, onToggle = FirebaseFloatingWindowState::setVisible)
        ErrorBox(modifier = Modifier.fillMaxSize(), error = state.error) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                if (state.events.isEmpty()) {
                    item {
                        Text(
                            text = "No analytics events yet",
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }
                } else {
                    items(state.events) { event ->
                        AnalyticsEventItem(event)
                    }
                }
            }
        }
    }
}

@Composable
private fun FloatingWindowToggle(visible: Boolean, onToggle: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text("Floating window", style = MaterialTheme.typography.bodyLarge)
        Switch(checked = visible, onCheckedChange = onToggle)
    }
}

@Composable
private fun AnalyticsEventItem(event: AnalyticsEvent) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "${DateUtils.formatLogTime(event.timestamp)} · ${event.name}",
                style = MaterialTheme.typography.titleMedium,
            )
            if (event.params.isNotEmpty()) {
                event.params.forEach { (key, value) ->
                    Text(
                        text = "$key = $value",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }
    }
}
