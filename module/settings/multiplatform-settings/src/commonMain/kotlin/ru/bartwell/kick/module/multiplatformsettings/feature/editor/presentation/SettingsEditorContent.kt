package ru.bartwell.kick.module.multiplatformsettings.feature.editor.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SettingsEditorContent(
    component: SettingsEditorComponent,
    modifier: Modifier = Modifier,
) {
    val state by component.model.subscribeAsState()

    Column(modifier = modifier) {
        TopAppBar(
            title = { Text(state.storageName) },
            navigationIcon = {
                IconButton(onClick = component::onBackPressed, modifier = Modifier.testTag("back")) {
                    Icon(imageVector = Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                IconButton(onClick = component::onSavePressed, modifier = Modifier.testTag("save")) {
                    Icon(imageVector = Icons.Default.Save, contentDescription = "Save")
                }
            },
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize()
                .padding(16.dp),
            state = rememberLazyListState(),
        ) {
            items(state.entries.entries.toList()) { (key, value) ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        modifier = Modifier.weight(1f).testTag("entry_" + key),
                        value = value,
                        onValueChange = { newValue ->
                            component.onValueChange(key, newValue)
                        },
                        label = { Text(text = key) },
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    IconButton(
                        onClick = { component.onDeleteClick(key) },
                        modifier = Modifier.testTag("delete_" + key)
                    ) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
