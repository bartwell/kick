package ru.bartwell.kick.module.controlpanel.core.persists

import com.russhwolf.settings.Settings
import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.module.controlpanel.data.ControlPanelItem
import ru.bartwell.kick.module.controlpanel.data.InputType

internal object ControlPanelSettings {
    private var settings: Settings? = null
    private var defaults: Map<String, InputType> = emptyMap()

    operator fun invoke(context: PlatformContext, configuration: List<ControlPanelItem>) {
        settings = PlatformSettingsFactory.create(
            context = context,
            name = "kick_control_panel_prefs",
        )

        defaults = configuration
            .mapNotNull { item -> (item.type as? InputType)?.let { item.name to it } }
            .toMap()
    }

    inline fun <reified V : InputType> put(key: String, value: V) {
        settings?.putInputType(key, value)
    }

    inline fun <reified V : InputType> get(key: String): V {
        val currentSettings = settings ?: return fallbackValue(key)
        val defaultValue: V = getDefault(key)
        return currentSettings.getInputType(key, defaultValue) as V
    }

    inline fun <reified V : InputType> getOrNull(key: String): V? {
        val currentSettings = settings ?: return null
        return currentSettings.getInputTypeOrNull<V>(key)
    }

    private fun Settings.putInputType(key: String, value: InputType) {
        when (value) {
            is InputType.Boolean -> putBoolean(key, value.value)
            is InputType.Int -> putInt(key, value.value)
            is InputType.Long -> putLong(key, value.value)
            is InputType.Float -> putFloat(key, value.value)
            is InputType.Double -> putDouble(key, value.value)
            is InputType.String -> putString(key, value.value)
        }
    }

    private fun Settings.getInputType(key: String, type: InputType): InputType {
        return when (type) {
            is InputType.Boolean -> InputType.Boolean(getBoolean(key, type.value))
            is InputType.Int -> InputType.Int(getInt(key, type.value))
            is InputType.Long -> InputType.Long(getLong(key, type.value))
            is InputType.Float -> InputType.Float(getFloat(key, type.value))
            is InputType.Double -> InputType.Double(getDouble(key, type.value))
            is InputType.String -> InputType.String(getString(key, type.value))
        }
    }

    private inline fun <reified V : InputType> Settings.getInputTypeOrNull(key: String): V? {
        val input: InputType? = when (V::class) {
            InputType.Boolean::class -> getBooleanOrNull(key)?.let(InputType::Boolean)
            InputType.Int::class -> getIntOrNull(key)?.let(InputType::Int)
            InputType.Long::class -> getLongOrNull(key)?.let(InputType::Long)
            InputType.Float::class -> getFloatOrNull(key)?.let(InputType::Float)
            InputType.Double::class -> getDoubleOrNull(key)?.let(InputType::Double)
            InputType.String::class -> getStringOrNull(key)?.let(InputType::String)
            else -> null
        }
        return input as? V
    }

    private inline fun <reified V : InputType> fallbackValue(key: String): V {
        val default = defaults[key]
        if (default is V) return default

        return when (V::class) {
            InputType.Boolean::class -> InputType.Boolean(false)
            InputType.Int::class -> InputType.Int(0)
            InputType.Long::class -> InputType.Long(0)
            InputType.Float::class -> InputType.Float(0f)
            InputType.Double::class -> InputType.Double(0.0)
            InputType.String::class -> InputType.String("")
            else -> error("Unsupported type: ${V::class.simpleName}")
        } as V
    }

    private inline fun <reified T : InputType> getDefault(key: String): T {
        val default = defaults[key]
            ?: error("Key \"$key\" is not defined in configuration")
        return default as? T
            ?: error("Requested ${T::class.simpleName} but default is ${default::class.simpleName}")
    }
}
