package ru.bartwell.kick.module.controlpanel.core.persists

import com.russhwolf.settings.Settings
import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.module.controlpanel.data.ControlPanelItem
import ru.bartwell.kick.module.controlpanel.data.InputType

internal object ControlPanelSettings {
    private lateinit var settings: Settings
    private var defaults = emptyMap<String, InputType>()

    operator fun invoke(context: PlatformContext, configuration: List<ControlPanelItem>) {
        settings = PlatformSettingsFactory.create(context = context, name = "kick_control_panel_prefs")
        defaults = configuration.mapNotNull { item ->
            val t = item.type
            if (t is InputType) item.name to t else null
        }.toMap()
    }

    inline fun <reified V : InputType> put(key: String, value: V) {
        when (value) {
            is InputType.Boolean -> settings.putBoolean(key, value.value)
            is InputType.Int -> settings.putInt(key, value.value)
            is InputType.Long -> settings.putLong(key, value.value)
            is InputType.Float -> settings.putFloat(key, value.value)
            is InputType.Double -> settings.putDouble(key, value.value)
            is InputType.String -> settings.putString(key, value.value)
        }
    }

    inline fun <reified V : InputType> get(key: String): V {
        return when (V::class) {
            InputType.Boolean::class ->
                InputType.Boolean(
                    settings.getBoolean(key, getDefault<InputType.Boolean>(key).value)
                )

            InputType.Int::class ->
                InputType.Int(
                    settings.getInt(key, getDefault<InputType.Int>(key).value)
                )

            InputType.Long::class ->
                InputType.Long(
                    settings.getLong(key, getDefault<InputType.Long>(key).value)
                )

            InputType.Float::class ->
                InputType.Float(
                    settings.getFloat(key, getDefault<InputType.Float>(key).value)
                )

            InputType.Double::class ->
                InputType.Double(
                    settings.getDouble(key, getDefault<InputType.Double>(key).value)
                )

            InputType.String::class ->
                InputType.String(
                    settings.getString(key, getDefault<InputType.String>(key).value)
                )

            else -> error("Unsupported type: ${V::class.simpleName}")
        } as V
    }

    inline fun <reified V : InputType> getOrNull(key: String): V? {
        return when (V::class) {
            InputType.Boolean::class -> settings.getBooleanOrNull(key)
            InputType.Int::class -> settings.getIntOrNull(key)
            InputType.Long::class -> settings.getLongOrNull(key)
            InputType.Float::class -> settings.getFloatOrNull(key)
            InputType.Double::class -> settings.getDoubleOrNull(key)
            InputType.String::class -> settings.getStringOrNull(key)
            else -> null
        } as? V
    }

    private inline fun <reified T : InputType> getDefault(key: String): T {
        val default = defaults[key]
            ?: error("Key \"$key\" is not defined in configuration")
        return default as? T
            ?: error("Requested ${T::class.simpleName} but default is ${default::class.simpleName}")
    }
}
