package ru.bartwell.kick.module.configuration.internal

import com.russhwolf.settings.Settings
import com.russhwolf.settings.*
import ru.bartwell.kick.module.configuration.data.ConfigurationItem
import ru.bartwell.kick.module.configuration.data.ValueType

internal object ConfigHolder {
    var settings: Settings? = null
    var items: Map<String, ConfigurationItem> = emptyMap()

    fun getValue(name: String): ValueType? {
        val settings = settings ?: return null
        val item = items[name] ?: return null
        return when (val def = item.default) {
            is ValueType.Bool -> ValueType.Bool(settings.getBoolean(name, def.value))
            is ValueType.Int -> ValueType.Int(settings.getInt(name, def.value))
            is ValueType.Long -> ValueType.Long(settings.getLong(name, def.value))
            is ValueType.Float -> ValueType.Float(settings.getFloat(name, def.value))
            is ValueType.Double -> ValueType.Double(settings.getDouble(name, def.value))
            is ValueType.Str -> ValueType.Str(settings.getString(name, def.value))
        }
    }

    fun getValueOrDefault(name: String): ValueType {
        val settings = settings
        val item = items[name] ?: return ValueType.Str("")
        if (settings == null) return item.default
        return when (val def = item.default) {
            is ValueType.Bool -> ValueType.Bool(settings.getBoolean(name, def.value))
            is ValueType.Int -> ValueType.Int(settings.getInt(name, def.value))
            is ValueType.Long -> ValueType.Long(settings.getLong(name, def.value))
            is ValueType.Float -> ValueType.Float(settings.getFloat(name, def.value))
            is ValueType.Double -> ValueType.Double(settings.getDouble(name, def.value))
            is ValueType.Str -> ValueType.Str(settings.getString(name, def.value))
        }
    }
}
