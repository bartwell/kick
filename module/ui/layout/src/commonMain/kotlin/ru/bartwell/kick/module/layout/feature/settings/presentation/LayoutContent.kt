package ru.bartwell.kick.module.layout.feature.settings.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import ru.bartwell.kick.core.data.platformContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun LayoutContent(
    component: LayoutComponent,
    modifier: Modifier = Modifier
) {
    val state = component.model.subscribeAsState()
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
                title = { Text("Layout") },
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
                headlineContent = { Text(text = "Enable") },
                trailingContent = {
                    Switch(
                        checked = state.value.enabled,
                        onCheckedChange = { enabled -> component.onEnabledChange(context, enabled) }
                    )
                },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            Text(
                text = "Desktop: ⌘⌥⇧K (macOS) / Ctrl+Alt+Shift+K (Windows/Linux)",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            Text(
                text = "Android/iOS: shake the device to open the inspector",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Text(
                text = "Triggers work only when the module is enabled",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}
