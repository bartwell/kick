package ru.bartwell.kick.module.configuration.data

import ru.bartwell.kick.module.configuration.persists.ConfigurationSettings

@Suppress("TooManyFunctions")
public class ConfigurationAccessor internal constructor() {

    public fun getBoolean(key: String): Boolean = ConfigurationSettings.get<ValueType.Boolean>(key).value
    public fun getBooleanOrNull(key: String): Boolean? = ConfigurationSettings.getOrNull<ValueType.Boolean>(key)?.value

    public fun getInt(key: String): Int = ConfigurationSettings.get<ValueType.Int>(key).value
    public fun getIntOrNull(key: String): Int? = ConfigurationSettings.getOrNull<ValueType.Int>(key)?.value

    public fun getLong(key: String): Long = ConfigurationSettings.get<ValueType.Long>(key).value
    public fun getLongOrNull(key: String): Long? = ConfigurationSettings.getOrNull<ValueType.Long>(key)?.value

    public fun getFloat(key: String): Float = ConfigurationSettings.get<ValueType.Float>(key).value
    public fun getFloatOrNull(key: String): Float? = ConfigurationSettings.getOrNull<ValueType.Float>(key)?.value

    public fun getDouble(key: String): Double = ConfigurationSettings.get<ValueType.Double>(key).value
    public fun getDoubleOrNull(key: String): Double? = ConfigurationSettings.getOrNull<ValueType.Double>(key)?.value

    public fun getString(key: String): String = ConfigurationSettings.get<ValueType.String>(key).value
    public fun getStringOrNull(key: String): String? = ConfigurationSettings.getOrNull<ValueType.String>(key)?.value
}
