package ru.bartwell.kick.module.sqlite.runtime.feature.update.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import ru.bartwell.kick.core.presentation.CheckboxWithText
import ru.bartwell.kick.core.presentation.ErrorBox
import ru.bartwell.kick.module.sqlite.core.data.ColumnType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun UpdateContent(
    component: UpdateComponent,
    modifier: Modifier = Modifier,
) {
    val state by component.model.subscribeAsState()
    Column(modifier = modifier) {
        TopAppBar(
            title = { Text(state.column.name + " (" + state.column.type.name.lowercase() + ")") },
            navigationIcon = {
                IconButton(onClick = component::onBackPressed) {
                    Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                }
            },
        )

        ErrorBox(state.loadError) {
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier.fillMaxWidth()
                    .verticalScroll(scrollState),
            ) {
                ValueTextField(
                    state = state,
                    onValueChange = component::onValueChange,
                    onSaveClick = component::onSaveClick,
                )
                if (!state.column.isNotNullable) {
                    CheckboxWithText(
                        modifier = Modifier.offset(y = (-8).dp),
                        text = "null",
                        isChecked = state.isNull,
                        onClick = component::onNullCheckboxClick,
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    onClick = component::onSaveClick,
                ) {
                    Text("Save")
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun ValueTextField(
    state: UpdateState,
    onValueChange: (String) -> Unit,
    onSaveClick: () -> Unit,
) {
    val keyboardOptions: KeyboardOptions
    val keyboardActions: KeyboardActions
    val singleLine: Boolean
    when (state.column.type) {
        ColumnType.INTEGER -> {
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done,
            )
            keyboardActions = KeyboardActions(onDone = { onSaveClick() })
            singleLine = true
        }

        ColumnType.REAL -> {
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Done,
            )
            keyboardActions = KeyboardActions(onDone = { onSaveClick() })
            singleLine = true
        }

        ColumnType.TEXT, ColumnType.BLOB -> {
            keyboardOptions = KeyboardOptions.Default
            keyboardActions = KeyboardActions.Default
            singleLine = false
        }
    }

    OutlinedTextField(
        modifier = Modifier.fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp),
        value = state.value.orEmpty(),
        onValueChange = onValueChange,
        enabled = !state.isNull,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        isError = state.saveError != null,
        supportingText = { Text(text = state.saveError.orEmpty()) },
    )
}
