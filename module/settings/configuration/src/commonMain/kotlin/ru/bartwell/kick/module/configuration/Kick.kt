package ru.bartwell.kick.module.configuration

import ru.bartwell.kick.Kick
import ru.bartwell.kick.module.configuration.data.ValueType
import ru.bartwell.kick.module.configuration.internal.ConfigHolder

public fun Kick.Companion.config(name: String): ValueType? =
    ConfigHolder.getValue(name)

public fun Kick.Companion.configOrDefault(name: String): ValueType =
    ConfigHolder.getValueOrDefault(name)

@Suppress("TooManyFunctions")
public object ConfigurationAccessor {
    public fun getBooleanOrNull(name: String): Boolean? =
        (Kick.config(name) as? ValueType.Bool)?.value
    public fun getBooleanOrDefault(name: String): Boolean =
        (Kick.config(name) as? ValueType.Bool)?.value
            ?: (ConfigHolder.items[name]?.default as? ValueType.Bool)?.value ?: false

    public fun getIntOrNull(name: String): Int? =
        (Kick.config(name) as? ValueType.Int)?.value
    public fun getIntOrDefault(name: String): Int =
        (Kick.config(name) as? ValueType.Int)?.value
            ?: (ConfigHolder.items[name]?.default as? ValueType.Int)?.value ?: 0

    public fun getLongOrNull(name: String): Long? =
        (Kick.config(name) as? ValueType.Long)?.value
    public fun getLongOrDefault(name: String): Long =
        (Kick.config(name) as? ValueType.Long)?.value
            ?: (ConfigHolder.items[name]?.default as? ValueType.Long)?.value ?: 0L

    public fun getFloatOrNull(name: String): Float? =
        (Kick.config(name) as? ValueType.Float)?.value
    public fun getFloatOrDefault(name: String): Float =
        (Kick.config(name) as? ValueType.Float)?.value
            ?: (ConfigHolder.items[name]?.default as? ValueType.Float)?.value ?: 0f

    public fun getDoubleOrNull(name: String): Double? =
        (Kick.config(name) as? ValueType.Double)?.value
    public fun getDoubleOrDefault(name: String): Double =
        (Kick.config(name) as? ValueType.Double)?.value
            ?: (ConfigHolder.items[name]?.default as? ValueType.Double)?.value ?: 0.0

    public fun getStringOrNull(name: String): String? =
        (Kick.config(name) as? ValueType.Str)?.value
    public fun getStringOrDefault(name: String): String =
        (Kick.config(name) as? ValueType.Str)?.value
            ?: (ConfigHolder.items[name]?.default as? ValueType.Str)?.value ?: ""
}

public val Kick.Companion.configuration: ConfigurationAccessor
    get() = ConfigurationAccessor
