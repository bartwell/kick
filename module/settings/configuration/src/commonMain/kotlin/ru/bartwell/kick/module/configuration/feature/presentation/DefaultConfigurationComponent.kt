package ru.bartwell.kick.module.configuration.feature.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import ru.bartwell.kick.module.configuration.core.persists.ConfigurationSettings
import ru.bartwell.kick.module.configuration.data.ConfigurationItem
import ru.bartwell.kick.module.configuration.data.ValueType

internal class DefaultConfigurationComponent(
    componentContext: ComponentContext,
    private val items: List<ConfigurationItem>,
    private val onFinished: () -> Unit,
) : ConfigurationComponent, ComponentContext by componentContext {

    private val _model = MutableValue(ConfigurationState(items, loadValues()))
    override val model: Value<ConfigurationState> = _model

    override fun onBackPressed() {
        onFinished()
    }

    override fun onSavePressed() {
        for ((name, value) in model.value.values) {
            ConfigurationSettings.put(name, value)
        }
        _model.value = model.value.copy(values = loadValues())
    }

    override fun onValueChange(name: String, value: ValueType) {
        val map = model.value.values.toMutableMap()
        map[name] = value
        _model.value = model.value.copy(values = map)
    }

    private fun loadValues(): Map<String, ValueType> =
        items.associate { item ->
            val name = item.name
            val defValue: ValueType = when (item.default) {
                is ValueType.Boolean -> ConfigurationSettings.get<ValueType.Boolean>(name)
                is ValueType.Int -> ConfigurationSettings.get<ValueType.Int>(name)
                is ValueType.Long -> ConfigurationSettings.get<ValueType.Long>(name)
                is ValueType.Float -> ConfigurationSettings.get<ValueType.Float>(name)
                is ValueType.Double -> ConfigurationSettings.get<ValueType.Double>(name)
                is ValueType.String -> ConfigurationSettings.get<ValueType.String>(name)
            }
            name to defValue
        }
}
