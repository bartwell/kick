package ru.bartwell.kick.module.configuration.feature.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import ru.bartwell.kick.module.configuration.data.Editor
import ru.bartwell.kick.module.configuration.data.ValueType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Suppress("LongMethod", "CyclomaticComplexMethod")
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
                    is ValueType.Bool -> {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(item.name, modifier = Modifier.weight(1f))
                            Switch(
                                checked = (value as? ValueType.Bool)?.value ?: def.value,
                                onCheckedChange = { component.onValueChange(item.name, ValueType.Bool(it)) },
                            )
                        }
                    }
                    is ValueType.Int, is ValueType.Long, is ValueType.Float, is ValueType.Double -> {
                        when (val editor = item.editor) {
                            is Editor.List -> {
                                var expanded by remember { mutableStateOf(false) }
                                val selected = (value ?: def) as ValueType
                                ExposedDropdownMenuBox(
                                    expanded = expanded,
                                    onExpandedChange = { expanded = !expanded }
                                ) {
                                    TextField(
                                        value = when (selected) {
                                            is ValueType.Int -> selected.value.toString()
                                            is ValueType.Long -> selected.value.toString()
                                            is ValueType.Float -> selected.value.toString()
                                            is ValueType.Double -> selected.value.toString()
                                            else -> ""
                                        },
                                        onValueChange = { },
                                        label = { Text(item.name) },
                                        readOnly = true,
                                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                                        colors = ExposedDropdownMenuDefaults.textFieldColors(),
                                        modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryNotEditable),
                                    )
                                    ExposedDropdownMenu(
                                        expanded = expanded,
                                        onDismissRequest = { expanded = false }
                                    ) {
                                        editor.options.forEach { option ->
                                            DropdownMenuItem(
                                                text = { Text(option.toString()) },
                                                onClick = {
                                                    component.onValueChange(item.name, option)
                                                    expanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                            is Editor.InputNumber, null -> {
                                val currentText = when (value) {
                                    is ValueType.Int -> value.value.toString()
                                    is ValueType.Long -> value.value.toString()
                                    is ValueType.Float -> value.value.toString()
                                    is ValueType.Double -> value.value.toString()
                                    else -> when (def) {
                                        is ValueType.Int -> def.value.toString()
                                        is ValueType.Long -> def.value.toString()
                                        is ValueType.Float -> def.value.toString()
                                        is ValueType.Double -> def.value.toString()
                                        else -> ""
                                    }
                                }
                                OutlinedTextField(
                                    value = currentText,
                                    onValueChange = { text ->
                                        val d = text.toDoubleOrNull() ?: 0.0
                                        val min = (editor as? Editor.InputNumber)?.min
                                        val max = (editor as? Editor.InputNumber)?.max
                                        var num = d
                                        if (min != null && num < min) num = min
                                        if (max != null && num > max) num = max
                                        val vt: ValueType = when (def) {
                                            is ValueType.Int -> ValueType.Int(num.toInt())
                                            is ValueType.Long -> ValueType.Long(num.toLong())
                                            is ValueType.Float -> ValueType.Float(num.toFloat())
                                            is ValueType.Double -> ValueType.Double(num)
                                            else -> def
                                        }
                                        component.onValueChange(item.name, vt)
                                    },
                                    label = { Text(item.name) },
                                )
                            }
                            else -> {}
                        }
                    }
                    is ValueType.Str -> {
                        when (val editor = item.editor) {
                            is Editor.List -> {
                                var expanded by remember { mutableStateOf(false) }
                                val selected = (value ?: def) as ValueType.Str
                                ExposedDropdownMenuBox(
                                    expanded = expanded,
                                    onExpandedChange = { expanded = !expanded }
                                ) {
                                    TextField(
                                        value = selected.value,
                                        onValueChange = { },
                                        label = { Text(item.name) },
                                        readOnly = true,
                                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                                        colors = ExposedDropdownMenuDefaults.textFieldColors(),
                                        modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryNotEditable),
                                    )
                                    ExposedDropdownMenu(
                                        expanded = expanded,
                                        onDismissRequest = { expanded = false }
                                    ) {
                                        editor.options.forEach { option ->
                                            val text = (option as? ValueType.Str)?.value ?: option.toString()
                                            DropdownMenuItem(
                                                text = { Text(text) },
                                                onClick = {
                                                    component.onValueChange(item.name, ValueType.Str(text))
                                                    expanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                            is Editor.InputString, null -> {
                                OutlinedTextField(
                                    value = (value as? ValueType.Str)?.value ?: def.value,
                                    onValueChange = { component.onValueChange(item.name, ValueType.Str(it)) },
                                    label = { Text(item.name) },
                                    singleLine = (editor as? Editor.InputString)?.singleLine ?: true,
                                )
                            }
                            else -> {}
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
