package ru.bartwell.kick.module.configuration.feature.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.russhwolf.settings.Settings
import com.russhwolf.settings.*
import ru.bartwell.kick.module.configuration.data.ConfigurationItem
import ru.bartwell.kick.module.configuration.data.ValueType

internal class DefaultConfigurationComponent(
    componentContext: ComponentContext,
    private val settings: Settings,
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
            when (value) {
                is ValueType.Bool -> settings.putBoolean(name, value.value)
                is ValueType.Int -> settings.putInt(name, value.value)
                is ValueType.Long -> settings.putLong(name, value.value)
                is ValueType.Float -> settings.putFloat(name, value.value)
                is ValueType.Double -> settings.putDouble(name, value.value)
                is ValueType.Str -> settings.putString(name, value.value)
            }
        }
        _model.value = _model.value.copy(values = loadValues())
    }

    override fun onValueChange(name: String, value: ValueType) {
        val map = _model.value.values.toMutableMap()
        map[name] = value
        _model.value = _model.value.copy(values = map)
    }

    private fun loadValues(): Map<String, ValueType> =
        items.associate { item ->
            val name = item.name
            when (val def = item.default) {
                is ValueType.Bool -> name to ValueType.Bool(settings.getBoolean(name, def.value))
                is ValueType.Int -> name to ValueType.Int(settings.getInt(name, def.value))
                is ValueType.Long -> name to ValueType.Long(settings.getLong(name, def.value))
                is ValueType.Float -> name to ValueType.Float(settings.getFloat(name, def.value))
                is ValueType.Double -> name to ValueType.Double(settings.getDouble(name, def.value))
                is ValueType.Str -> name to ValueType.Str(settings.getString(name, def.value))
            }
        }
}
