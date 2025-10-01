package ru.bartwell.kick.module.sqlite.runtime.feature.query.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import ru.bartwell.kick.module.sqlite.runtime.feature.viewer.presentation.table.Table

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun QueryContent(
    component: QueryComponent,
    modifier: Modifier = Modifier,
) {
    val state by component.model.subscribeAsState()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column(modifier = modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Query") },
            navigationIcon = {
                IconButton(onClick = component::onBackPressed) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            },
        )

        Column(modifier = Modifier.fillMaxSize()) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .focusRequester(focusRequester)
                    .testTag("query_input"),
                value = state.query,
                onValueChange = component::onQueryChange,
            )

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .testTag("execute_btn"),
                onClick = component::onExecuteClick,
            ) {
                Text("Execute")
            }

            Spacer(modifier = Modifier.height(32.dp))

            if (state.message.isEmpty()) {
                Table(rows = state.rows)
            } else {
                val color = if (state.isError) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.onBackground
                }
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp).testTag("query_message"),
                    text = state.message,
                    style = MaterialTheme.typography.bodyLarge,
                    color = color,
                )
            }
        }
    }
}
