package ru.bartwell.kick.module.configuration.core.persists

import com.russhwolf.settings.Settings
import ru.bartwell.kick.module.configuration.data.ConfigurationItem
import ru.bartwell.kick.module.configuration.data.ValueType

internal object ConfigurationSettings {
    private val settings = Settings()
    private var defaults = emptyMap<String, ValueType>()

    operator fun invoke(configuration: List<ConfigurationItem>) {
        defaults = configuration.associate { it.name to it.default }
    }

    inline fun <reified V : ValueType> put(key: String, value: V) {
        when (value) {
            is ValueType.Boolean -> settings.putBoolean(key, value.value)
            is ValueType.Int -> settings.putInt(key, value.value)
            is ValueType.Long -> settings.putLong(key, value.value)
            is ValueType.Float -> settings.putFloat(key, value.value)
            is ValueType.Double -> settings.putDouble(key, value.value)
            is ValueType.String -> settings.putString(key, value.value)
        }
    }

    inline fun <reified V : ValueType> get(key: String): V {
        return when (V::class) {
            ValueType.Boolean::class ->
                ValueType.Boolean(
                    settings.getBoolean(key, getDefault<ValueType.Boolean>(key).value)
                )

            ValueType.Int::class ->
                ValueType.Int(
                    settings.getInt(key, getDefault<ValueType.Int>(key).value)
                )

            ValueType.Long::class ->
                ValueType.Long(
                    settings.getLong(key, getDefault<ValueType.Long>(key).value)
                )

            ValueType.Float::class ->
                ValueType.Float(
                    settings.getFloat(key, getDefault<ValueType.Float>(key).value)
                )

            ValueType.Double::class ->
                ValueType.Double(
                    settings.getDouble(key, getDefault<ValueType.Double>(key).value)
                )

            ValueType.String::class ->
                ValueType.String(
                    settings.getString(key, getDefault<ValueType.String>(key).value)
                )

            else -> error("Unsupported type: ${V::class.simpleName}")
        } as V
    }

    inline fun <reified V : ValueType> getOrNull(key: String): V? {
        return when (V::class) {
            ValueType.Boolean::class -> settings.getBooleanOrNull(key)
            ValueType.Int::class -> settings.getIntOrNull(key)
            ValueType.Long::class -> settings.getLongOrNull(key)
            ValueType.Float::class -> settings.getFloatOrNull(key)
            ValueType.Double::class -> settings.getDoubleOrNull(key)
            ValueType.String::class -> settings.getStringOrNull(key)
            else -> null
        } as? V
    }

    private inline fun <reified T : ValueType> getDefault(key: String): T {
        val default = defaults[key]
            ?: error("Key \"$key\" is not defined in configuration")
        return default as? T
            ?: error("Requested ${T::class.simpleName} but default is ${default::class.simpleName}")
    }
}
