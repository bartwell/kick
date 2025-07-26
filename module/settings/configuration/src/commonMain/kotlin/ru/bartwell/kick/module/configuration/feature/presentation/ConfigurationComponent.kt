package ru.bartwell.kick.module.configuration.feature.presentation

import com.arkivanov.decompose.value.Value
import ru.bartwell.kick.core.component.Component
import ru.bartwell.kick.module.configuration.data.ConfigurationItem
import ru.bartwell.kick.module.configuration.data.ValueType

internal data class ConfigurationState(
    val items: List<ConfigurationItem>,
    val values: Map<String, ValueType>,
)

internal interface ConfigurationComponent : Component {
    val model: Value<ConfigurationState>
    fun onBackPressed()
    fun onSavePressed()
    fun onValueChange(name: String, value: ValueType)
}
