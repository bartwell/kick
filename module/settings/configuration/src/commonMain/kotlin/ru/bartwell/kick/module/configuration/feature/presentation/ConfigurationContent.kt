package ru.bartwell.kick.module.configuration.feature.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import ru.bartwell.kick.module.configuration.data.ConfigurationItem
import ru.bartwell.kick.module.configuration.data.Editor
import ru.bartwell.kick.module.configuration.data.ValueType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ConfigurationContent(
    component: ConfigurationComponent,
    modifier: Modifier = Modifier,
) {
    val state by component.model.subscribeAsState()

    Column(modifier = modifier) {
        TopAppBar(
            title = { Text("Configuration") },
            navigationIcon = {
                IconButton(onClick = component::onBackPressed) {
                    Icon(imageVector = Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                IconButton(onClick = component::onSavePressed) {
                    Icon(imageVector = Icons.Default.Save, contentDescription = "Save")
                }
            },
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            state = rememberLazyListState(),
        ) {
            items(state.items) { item ->
                val value = state.values[item.name]
                when (val def = item.default) {
                    is ValueType.Boolean -> BooleanItem(item, value, def) {
                        component.onValueChange(item.name, ValueType.Boolean(it))
                    }

                    is ValueType.Int,
                    is ValueType.Long,
                    is ValueType.Float,
                    is ValueType.Double -> NumberItem(item, value, def) { name, vt ->
                        component.onValueChange(name, vt)
                    }

                    is ValueType.String -> StringItem(item, value, def) { name, vt ->
                        component.onValueChange(name, vt)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StringItem(
    item: ConfigurationItem,
    value: ValueType?,
    def: ValueType.String,
    onValueChange: (String, ValueType) -> Unit
) {
    when (val editor = item.editor) {
        is Editor.List -> {
            var expanded by remember { mutableStateOf(false) }
            val selected = (value ?: def) as ValueType.String
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(type = MenuAnchorType.PrimaryNotEditable),
                    value = selected.value,
                    onValueChange = { },
                    label = { Text(item.name) },
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                    colors = ExposedDropdownMenuDefaults.textFieldColors(),
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    editor.options.forEach { option ->
                        val text = (option as? ValueType.String)?.value ?: option.toString()
                        DropdownMenuItem(
                            text = { Text(text) },
                            onClick = {
                                onValueChange(item.name, ValueType.String(text))
                                expanded = false
                            }
                        )
                    }
                }
            }
        }

        is Editor.InputString, null -> {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = (value as? ValueType.String)?.value ?: def.value,
                onValueChange = { onValueChange(item.name, ValueType.String(it)) },
                label = { Text(item.name) },
                singleLine = editor?.singleLine ?: true,
            )
        }

        else -> {}
    }
}

@Composable
private fun BooleanItem(
    item: ConfigurationItem,
    value: ValueType?,
    defValue: ValueType.Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(item.name, modifier = Modifier.weight(1f))
        Switch(
            checked = (value as? ValueType.Boolean)?.value ?: defValue.value,
            onCheckedChange = onCheckedChange,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NumberItem(
    item: ConfigurationItem,
    value: ValueType?,
    def: ValueType,
    onValueChange: (String, ValueType) -> Unit,
) {
    if (item.editor is Editor.List) {
        NumberItemList(
            name = item.name,
            options = item.editor.options,
            selected = value ?: def,
            onSelect = { onValueChange(item.name, it) }
        )
    } else {
        NumberItemInput(
            name = item.name,
            value = value,
            default = def,
            min = (item.editor as? Editor.InputNumber)?.min,
            max = (item.editor as? Editor.InputNumber)?.max,
            onValueChange = { onValueChange(item.name, it) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NumberItemList(
    name: String,
    options: List<ValueType>,
    selected: ValueType,
    onSelect: (ValueType) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(type = MenuAnchorType.PrimaryNotEditable),
            value = selected.asString(),
            onValueChange = { },
            label = { Text(name) },
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.asString()) },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NumberItemInput(
    name: String,
    value: ValueType?,
    default: ValueType,
    min: Double?,
    max: Double?,
    onValueChange: (ValueType) -> Unit,
) {
    val text = (value ?: default).asString()
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = text,
        onValueChange = { newText ->
            val parsed = newText.toDoubleOrNull() ?: 0.0
            val clipped = parsed.coerceIn(min ?: Double.NEGATIVE_INFINITY, max ?: Double.POSITIVE_INFINITY)
            onValueChange(default.toValueType(clipped))
        },
        label = { Text(name) },
    )
}

private fun ValueType.asString(): String = when (this) {
    is ValueType.Int -> value.toString()
    is ValueType.Long -> value.toString()
    is ValueType.Float -> value.toString()
    is ValueType.Double -> value.toString()
    else -> ""
}

private fun ValueType.toValueType(d: Double): ValueType = when (this) {
    is ValueType.Int -> ValueType.Int(d.toInt())
    is ValueType.Long -> ValueType.Long(d.toLong())
    is ValueType.Float -> ValueType.Float(d.toFloat())
    is ValueType.Double -> ValueType.Double(d)
    else -> this
}
