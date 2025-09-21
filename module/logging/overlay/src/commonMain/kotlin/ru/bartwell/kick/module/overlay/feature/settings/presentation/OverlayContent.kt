package ru.bartwell.kick.module.overlay.feature.settings.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import ru.bartwell.kick.core.data.platformContext
import ru.bartwell.kick.module.overlay.core.store.OverlayStore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun OverlayContent(
    component: OverlayComponent,
    modifier: Modifier = Modifier
) {
    val state = component.model.subscribeAsState()
    val items by OverlayStore.items.collectAsState()
    val context = platformContext()

    var launched by rememberSaveable { mutableStateOf(false) }
    if (!launched) {
        LaunchedEffect(Unit) {
            component.init(context)
            launched = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Overlay") },
                navigationIcon = {
                    IconButton(onClick = { component.onBackClick() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = modifier.padding(padding)) {
            ListItem(
                headlineContent = { Text(text = "Enable floating overlay") },
                trailingContent = {
                    Switch(
                        checked = state.value.enabled,
                        onCheckedChange = { enabled -> component.onEnabledChange(context, enabled) }
                    )
                },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            Text(
                text = "Properties: one per line",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(items) { (key, value) ->
                    Text(
                        text = "$key: $value",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}
