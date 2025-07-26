package ru.bartwell.kick.module.configuration

import ru.bartwell.kick.Kick
import ru.bartwell.kick.module.configuration.data.ValueType

@Suppress("UnusedPrivateProperty", "unused")
public fun Kick.Companion.config(name: String): ValueType? = null

@Suppress("UnusedPrivateProperty", "unused")
public fun Kick.Companion.configOrDefault(name: String): ValueType = ValueType.Str("")

public object ConfigurationAccessor {
    public fun getBooleanOrNull(name: String): Boolean? = null
    public fun getBooleanOrDefault(name: String): Boolean = false
    public fun getIntOrNull(name: String): Int? = null
    public fun getIntOrDefault(name: String): Int = 0
    public fun getLongOrNull(name: String): Long? = null
    public fun getLongOrDefault(name: String): Long = 0L
    public fun getFloatOrNull(name: String): Float? = null
    public fun getFloatOrDefault(name: String): Float = 0f
    public fun getDoubleOrNull(name: String): Double? = null
    public fun getDoubleOrDefault(name: String): Double = 0.0
    public fun getStringOrNull(name: String): String? = null
    public fun getStringOrDefault(name: String): String = ""
}

public val Kick.Companion.configuration: ConfigurationAccessor
    get() = ConfigurationAccessor
