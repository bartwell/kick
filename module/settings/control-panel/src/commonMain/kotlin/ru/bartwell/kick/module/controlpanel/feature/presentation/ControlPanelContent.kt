package ru.bartwell.kick.module.controlpanel.feature.presentation

import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import ru.bartwell.kick.core.ui.ExposedDropdownMenuBox
import ru.bartwell.kick.module.controlpanel.core.actions.ControlPanelActions
import ru.bartwell.kick.module.controlpanel.data.ActionType
import ru.bartwell.kick.module.controlpanel.data.ControlPanelItem
import ru.bartwell.kick.module.controlpanel.data.Editor
import ru.bartwell.kick.module.controlpanel.data.InputType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ControlPanelContent(
    component: ControlPanelComponent,
    modifier: Modifier = Modifier,
) {
    val state by component.model.subscribeAsState()

    Column(modifier = modifier) {
        TopAppBar(
            title = { Text("Control Panel") },
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
            modifier = Modifier.fillMaxSize().padding(16.dp).testTag("config_list"),
            state = rememberLazyListState(),
        ) {
            // Ungrouped items first
            items(state.items.filter { it.category.isNullOrBlank() }) { item ->
                RenderItemRow(item = item, value = state.values[item.name]) { name, newValue ->
                    component.onValueChange(name, newValue)
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Grouped items by category
            val grouped: Map<String, List<ControlPanelItem>> = state.items
                .filter { !it.category.isNullOrBlank() }
                .groupBy { it.category!!.trim() }

            val categories: List<String> = grouped.keys.map { it }.sortedBy { it.lowercase() }
            categories.forEach { category ->
                val expanded = state.expanded[category] ?: true
                val itemsInCategory: List<ControlPanelItem> = grouped[category] ?: emptyList()
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { component.onCategoryToggle(category) }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(category, modifier = Modifier.weight(1f))
                        Icon(
                            imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                            contentDescription = if (expanded) "Collapse" else "Expand",
                            modifier = Modifier.testTag("category_" + category)
                        )
                    }
                }
                if (expanded) {
                    items(itemsInCategory) { cpItem ->
                        RenderItemRow(item = cpItem, value = state.values[cpItem.name]) { name, newValue ->
                            component.onValueChange(name, newValue)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun RenderItemRow(
    item: ControlPanelItem,
    value: InputType?,
    onValueChange: (String, InputType) -> Unit,
) {
    when (val t = item.type) {
        is InputType.Boolean -> BooleanItem(item, value, t) {
            onValueChange(item.name, InputType.Boolean(it))
        }

        is InputType.Int,
        is InputType.Long,
        is InputType.Float,
        is InputType.Double -> NumberItem(item, value, t) { name, vt ->
            onValueChange(name, vt)
        }

        is InputType.String -> StringItem(item, value, t) { name, vt ->
            onValueChange(name, vt)
        }

        is ActionType.Button -> ActionItem(item.name, t.id)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StringItem(
    item: ControlPanelItem,
    value: InputType?,
    def: InputType.String,
    onValueChange: (String, InputType) -> Unit
) {
    when (val editor = item.editor) {
        is Editor.List -> {
            var expanded by remember { mutableStateOf(false) }
            val selected = (value ?: def) as InputType.String
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
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
                        val text = (option as? InputType.String)?.value ?: option.toString()
                        DropdownMenuItem(
                            text = { Text(text) },
                            onClick = {
                                onValueChange(item.name, InputType.String(text))
                                expanded = false
                            }
                        )
                    }
                }
            }
        }

        is Editor.InputString, null -> {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth().testTag("string_input_" + item.name),
                value = (value as? InputType.String)?.value ?: def.value,
                onValueChange = { onValueChange(item.name, InputType.String(it)) },
                label = { Text(item.name) },
                singleLine = editor?.singleLine ?: true,
            )
        }

        else -> {}
    }
}

@Composable
private fun BooleanItem(
    item: ControlPanelItem,
    value: InputType?,
    defValue: InputType.Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(item.name, modifier = Modifier.weight(1f))
        Switch(
            modifier = Modifier.testTag("bool_" + item.name),
            checked = (value as? InputType.Boolean)?.value ?: defValue.value,
            onCheckedChange = onCheckedChange,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NumberItem(
    item: ControlPanelItem,
    value: InputType?,
    def: InputType,
    onValueChange: (String, InputType) -> Unit,
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
    options: List<InputType>,
    selected: InputType,
    onSelect: (InputType) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("number_list_" + name)
                .menuAnchor(),
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
    value: InputType?,
    default: InputType,
    min: Double?,
    max: Double?,
    onValueChange: (InputType) -> Unit,
) {
    val text = (value ?: default).asString()
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth().testTag("number_input_" + name),
        value = text,
        onValueChange = { newText ->
            val parsed = newText.toDoubleOrNull() ?: 0.0
            val clipped = parsed.coerceIn(min ?: Double.NEGATIVE_INFINITY, max ?: Double.POSITIVE_INFINITY)
            onValueChange(default.toValueType(clipped))
        },
        label = { Text(name) },
    )
}

private fun InputType.asString(): String = when (this) {
    is InputType.Int -> value.toString()
    is InputType.Long -> value.toString()
    is InputType.Float -> value.toString()
    is InputType.Double -> value.toString()
    else -> ""
}

private fun InputType.toValueType(d: Double): InputType = when (this) {
    is InputType.Int -> InputType.Int(d.toInt())
    is InputType.Long -> InputType.Long(d.toLong())
    is InputType.Float -> InputType.Float(d.toFloat())
    is InputType.Double -> InputType.Double(d)
    else -> this
}

@Composable
private fun ActionItem(label: String, id: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(label, modifier = Modifier.weight(1f))
        Button(onClick = { ControlPanelActions.emitButtonClick(id) }, modifier = Modifier.testTag("action_" + id)) {
            Text("Run")
        }
    }
}
