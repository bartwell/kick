package ru.bartwell.kick.module.runner.feature.params.presentation

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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import ru.bartwell.kick.core.presentation.BackOrCloseButton
import ru.bartwell.kick.core.presentation.ErrorBox
import ru.bartwell.kick.module.runner.core.params.RunnerParameter
import ru.bartwell.kick.module.runner.core.params.RunnerParameterType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun RunnerParamsContent(
    component: RunnerParamsComponent,
    modifier: Modifier = Modifier,
) {
    val state by component.model.subscribeAsState()

    Column(modifier = modifier) {
        TopAppBar(
            title = { Text(state.title.ifBlank { "Parameters" }) },
            navigationIcon = { BackOrCloseButton(onBack = component::onBackPressed) },
        )
        if (state.isSubmitting) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
        ErrorBox(error = state.errorMessage, modifier = Modifier.fillMaxSize()) {
            if (state.params.isEmpty()) {
                Text(
                    modifier = Modifier.padding(16.dp),
                    text = "No parameters required."
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = rememberLazyListState(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp)
                ) {
                    items(state.params) { param ->
                        ParamItem(
                            param = param,
                            value = state.values[param.id],
                            error = state.errors[param.id],
                            onValueChange = component::onValueChange,
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                    item {
                        Button(
                            onClick = component::onSubmit,
                            enabled = !state.isSubmitting,
                            modifier = Modifier.fillMaxWidth(),
                        ) { Text("Run") }
                    }
                }
            }
        }
    }
}

@Composable
private fun ParamItem(
    param: RunnerParameter<*>,
    value: Any?,
    error: String?,
    onValueChange: (String, Any?) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    when (val type = param.type) {
        RunnerParameterType.BooleanType -> BooleanParam(param, value, onValueChange)
        is RunnerParameterType.StringType -> StringParam(
            param = param,
            value = value,
            error = error,
            onValueChange = onValueChange,
            focusManager = focusManager,
            multiline = type.multiline,
        )
        is RunnerParameterType.IntType -> NumberParam(
            param = param,
            value = value,
            error = error,
            keyboardType = KeyboardType.Number,
            focusManager = focusManager,
        ) { text ->
            onValueChange(param.id, text.toIntOrNull())
        }
        is RunnerParameterType.LongType -> NumberParam(
            param = param,
            value = value,
            error = error,
            keyboardType = KeyboardType.Number,
            focusManager = focusManager,
        ) { text ->
            onValueChange(param.id, text.toLongOrNull())
        }
        is RunnerParameterType.FloatType -> NumberParam(
            param = param,
            value = value,
            error = error,
            keyboardType = KeyboardType.Decimal,
            focusManager = focusManager,
        ) { text ->
            onValueChange(param.id, text.toFloatOrNull())
        }
        is RunnerParameterType.DoubleType -> NumberParam(
            param = param,
            value = value,
            error = error,
            keyboardType = KeyboardType.Decimal,
            focusManager = focusManager,
        ) { text ->
            onValueChange(param.id, text.toDoubleOrNull())
        }
        is RunnerParameterType.SingleChoice<*> -> SingleChoiceParam(param, value, type, onValueChange)
        is RunnerParameterType.MultiChoice<*> -> MultiChoiceParam(param, value, type, onValueChange)
    }
}

@Composable
private fun BooleanParam(
    param: RunnerParameter<*>,
    value: Any?,
    onValueChange: (String, Any?) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(
            checked = value as? Boolean ?: param.defaultValue as? Boolean ?: false,
            onCheckedChange = { onValueChange(param.id, it) },
        )
        Column(modifier = Modifier.padding(start = 8.dp)) {
            Text(param.title)
            param.description?.let {
                Text(it, style = androidx.compose.material3.MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun StringParam(
    param: RunnerParameter<*>,
    value: Any?,
    error: String?,
    onValueChange: (String, Any?) -> Unit,
    focusManager: FocusManager,
    multiline: Boolean,
) {
    OutlinedTextField(
        value = value as? String ?: param.defaultValue as? String ?: "",
        onValueChange = { onValueChange(param.id, it) },
        label = { Text(param.title) },
        supportingText = param.description?.let { description ->
            {
                Text(
                    description,
                    style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                )
            }
        },
        isError = error != null,
        singleLine = !multiline,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next,
        ),
        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
        modifier = Modifier.fillMaxWidth()
    )
    error?.let { Text(it, color = androidx.compose.material3.MaterialTheme.colorScheme.error) }
}

@Composable
private fun NumberParam(
    param: RunnerParameter<*>,
    value: Any?,
    error: String?,
    keyboardType: KeyboardType,
    focusManager: FocusManager,
    onValueChange: (String) -> Unit,
) {
    OutlinedTextField(
        value = value as? String ?: value?.toString() ?: "",
        onValueChange = onValueChange,
        label = { Text(param.title) },
        supportingText = param.description?.let { description ->
            {
                Text(
                    description,
                    style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                )
            }
        },
        isError = error != null,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
        modifier = Modifier.fillMaxWidth()
    )
    error?.let { Text(it, color = androidx.compose.material3.MaterialTheme.colorScheme.error) }
}

@Composable
private fun SingleChoiceParam(
    param: RunnerParameter<*>,
    value: Any?,
    type: RunnerParameterType.SingleChoice<*>,
    onValueChange: (String, Any?) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val items = type.options
    val selected = value ?: param.defaultValue ?: items.firstOrNull()
    Column {
        OutlinedTextField(
            value = selected?.toString() ?: "",
            onValueChange = { },
            readOnly = true,
            label = { Text(param.title) },
            supportingText = param.description?.let { description ->
                {
                    Text(
                        description,
                        style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp)
                .clickable { expanded = true },
            trailingIcon = {
                Icon(Icons.Outlined.Check, contentDescription = null)
            }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            items.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.toString()) },
                    onClick = {
                        expanded = false
                        onValueChange(param.id, option)
                    }
                )
            }
        }
    }
}

@Composable
private fun MultiChoiceParam(
    param: RunnerParameter<*>,
    value: Any?,
    type: RunnerParameterType.MultiChoice<*>,
    onValueChange: (String, Any?) -> Unit,
) {
    Column {
        Text(param.title)
        Spacer(modifier = Modifier.height(4.dp))
        val selected: Set<Any> = (value as? Set<*>)?.filterNotNull()?.toSet()
            ?: param.defaultValue as? Set<Any> ?: emptySet()
        type.options.forEach { option ->
            val checked = selected.contains(option)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable {
                        val next = if (checked) selected - option else selected + option
                        onValueChange(param.id, next)
                    },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Checkbox(
                    checked = checked,
                    onCheckedChange = {
                        val next = if (checked) selected - option else selected + option
                        onValueChange(param.id, next)
                    }
                )
                Text(text = option.toString(), modifier = Modifier.padding(start = 8.dp))
            }
        }
        param.description?.let {
            Text(
                it,
                modifier = Modifier.padding(top = 4.dp),
                style = androidx.compose.material3.MaterialTheme.typography.bodySmall
            )
        }
    }
}
