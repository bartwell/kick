package ru.bartwell.kick.module.layout.feature.properties.presentation

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import ru.bartwell.kick.core.data.platformContext
import ru.bartwell.kick.module.layout.feature.properties.extension.copyToClipboard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun LayoutPropertiesContent(
    component: LayoutPropertiesComponent,
    modifier: Modifier = Modifier,
) {
    val state = component.model.subscribeAsState()
    val context = platformContext()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Layout Properties") },
                navigationIcon = {
                    IconButton(onClick = component::onBackPressed) {
                        Icon(imageVector = Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        modifier = modifier
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(state.value.properties) { property ->
                ListItem(
                    headlineContent = { Text(property.name) },
                    supportingContent = { Text(property.value) },
                    trailingContent = {
                        IconButton(onClick = { context.copyToClipboard(property.value) }) {
                            Icon(imageVector = Icons.Filled.ContentCopy, contentDescription = "Copy value")
                        }
                    }
                )
            }
        }
    }
}
