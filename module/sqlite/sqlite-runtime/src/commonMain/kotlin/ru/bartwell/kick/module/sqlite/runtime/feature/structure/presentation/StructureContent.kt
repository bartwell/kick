package ru.bartwell.kick.module.sqlite.runtime.feature.structure.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import ru.bartwell.kick.core.presentation.ErrorBox

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun StructureContent(
    component: StructureComponent,
    modifier: Modifier = Modifier,
) {
    val state by component.model.subscribeAsState()
    Column(modifier = modifier) {
        TopAppBar(
            title = { Text(state.table) },
            navigationIcon = {
                IconButton(onClick = component::onBackPressed) {
                    Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                }
            }
        )
        ErrorBox(
            modifier = Modifier.fillMaxSize(),
            error = state.error
        ) {
            LazyColumn(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                items(state.columns) { column ->
                    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Name: ${column.name}",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Type: ${column.type}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "Not Null: ${if (column.isNotNullable) "Yes" else "No"}",
                                style = MaterialTheme.typography.bodySmall
                            )

                            if (column.defaultValue == null) {
                                Text(
                                    text = "Default value: null",
                                    style = MaterialTheme.typography.bodySmall,
                                )
                            } else {
                                var expanded by remember { mutableStateOf(false) }
                                Text(
                                    text = "Default value: " + column.defaultValue,
                                    style = MaterialTheme.typography.bodySmall,
                                    maxLines = if (expanded) Int.MAX_VALUE else 2,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.clickable { expanded = !expanded }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
