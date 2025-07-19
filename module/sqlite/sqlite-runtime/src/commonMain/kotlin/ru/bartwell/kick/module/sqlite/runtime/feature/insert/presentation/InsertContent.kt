package ru.bartwell.kick.module.sqlite.runtime.feature.insert.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import ru.bartwell.kick.core.presentation.ErrorAlert
import ru.bartwell.kick.module.sqlite.core.data.Column
import ru.bartwell.kick.module.sqlite.core.data.ColumnType
import ru.bartwell.kick.module.sqlite.runtime.feature.insert.data.InsertValueType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun InsertContent(
    component: InsertComponent,
    modifier: Modifier = Modifier,
) {
    val state by component.model.subscribeAsState()

    Column(modifier = modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Insert row") },
            navigationIcon = {
                IconButton(onClick = component::onBackPressed) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            },
        )

        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier.fillMaxWidth()
                .verticalScroll(scrollState)
        ) {
            val lastColumn = state.columns.last()
            state.columns.forEach { column ->
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    state = state,
                    column = column,
                    isLastColumn = column == lastColumn,
                    onValueChange = component::onValueChange,
                    onValueTypeChange = component::onValueTypeChange,
                    onSaveClick = component::onSaveClick
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                onClick = component::onSaveClick,
            ) {
                Text("Insert")
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    state.insertError?.let { error ->
        ErrorAlert(error, component::onAlertDismiss)
    }
}

@Composable
private fun Card(
    state: InsertState,
    column: Column,
    isLastColumn: Boolean,
    onValueChange: (Column, String) -> Unit,
    onValueTypeChange: (Column, InsertValueType) -> Unit,
    onSaveClick: () -> Unit,
) {
    val keyboardType: KeyboardType
    val isSingleLine: Boolean
    when (column.type) {
        ColumnType.INTEGER -> {
            keyboardType = KeyboardType.Number
            isSingleLine = true
        }

        ColumnType.REAL -> {
            keyboardType = KeyboardType.Decimal
            isSingleLine = true
        }

        ColumnType.TEXT, ColumnType.BLOB -> {
            keyboardType = KeyboardType.Text
            isSingleLine = false
        }
    }

    val imeAction: ImeAction
    val keyboardActions: KeyboardActions
    if (isLastColumn) {
        imeAction = ImeAction.Done
        keyboardActions = KeyboardActions(onDone = { onSaveClick() })
    } else {
        imeAction = if (column.type == ColumnType.TEXT) ImeAction.Default else ImeAction.Next
        keyboardActions = KeyboardActions.Default
    }
    OutlinedCard(
        modifier = Modifier.fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 16.dp)
                .padding(top = 16.dp, bottom = 8.dp),
            text = column.name + " (" + column.type.name.lowercase() + ")",
            style = MaterialTheme.typography.titleMedium,
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 16.dp),
            value = getFieldValue(state, column),
            onValueChange = { onValueChange(column, it) },
            enabled = state.valueTypes[column] == InsertValueType.VALUE,
            singleLine = isSingleLine,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
            keyboardActions = keyboardActions,
        )

        TypesGroup(
            column = column,
            selectedItem = state.valueTypes.getOrDefault(column),
            onChange = onValueTypeChange,
        )
    }
}

private fun getFieldValue(state: InsertState, column: Column): String {
    val nullValue = "[NULL]"
    return when (state.valueTypes.getOrDefault(column)) {
        InsertValueType.DEFAULT -> column.defaultValue ?: nullValue
        InsertValueType.NULL -> nullValue
        InsertValueType.VALUE -> state.values[column].orEmpty()
    }
}

@Composable
private fun TypesGroup(
    column: Column,
    selectedItem: InsertValueType,
    onChange: (Column, InsertValueType) -> Unit,
) {
    val items = InsertValueType.entries.filter { it != InsertValueType.NULL || !column.isNotNullable }
    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        items.forEach { item ->
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onChange(column, item) }
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                RadioButton(selected = selectedItem == item, onClick = null)
                Text(
                    text = item.title,
                    modifier = Modifier.padding(start = 4.dp),
                )
            }
        }
    }
}
