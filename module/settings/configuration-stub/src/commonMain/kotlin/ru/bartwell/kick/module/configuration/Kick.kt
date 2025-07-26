@file:Suppress("FunctionOnlyReturningConstant")
package ru.bartwell.kick.module.configuration

import ru.bartwell.kick.Kick
import ru.bartwell.kick.module.configuration.data.ValueType

private const val EMPTY = ""
private const val ZERO_INT: Int = 0
private const val ZERO_LONG: Long = 0L
private const val ZERO_FLOAT: Float = 0f
private const val ZERO_DOUBLE: Double = 0.0
private val NULL_INT: Int? = null
private val NULL_LONG: Long? = null
private val NULL_FLOAT: Float? = null
private val NULL_DOUBLE: Double? = null
private val NULL_STRING: String? = null
private val NULL_VALUE: ValueType? = null
private val EMPTY_VALUE: ValueType.Str = ValueType.Str(EMPTY)

@Suppress("UnusedPrivateProperty", "UnusedParameter", "unused")
public fun Kick.Companion.config(name: String): ValueType? = NULL_VALUE

@Suppress("UnusedPrivateProperty", "UnusedParameter", "unused")
public fun Kick.Companion.configOrDefault(name: String): ValueType = EMPTY_VALUE

@Suppress("TooManyFunctions")
public object ConfigurationAccessor {
    @Suppress("UnusedParameter")
    public fun getBooleanOrNull(name: String): Boolean? = null

    @Suppress("UnusedParameter")
    public fun getBooleanOrDefault(name: String): Boolean = false

    @Suppress("UnusedParameter")
    public fun getIntOrNull(name: String): Int? = NULL_INT

    @Suppress("UnusedParameter")
    public fun getIntOrDefault(name: String): Int = ZERO_INT

    @Suppress("UnusedParameter")
    public fun getLongOrNull(name: String): Long? = NULL_LONG

    @Suppress("UnusedParameter")
    public fun getLongOrDefault(name: String): Long = ZERO_LONG

    @Suppress("UnusedParameter")
    public fun getFloatOrNull(name: String): Float? = NULL_FLOAT

    @Suppress("UnusedParameter")
    public fun getFloatOrDefault(name: String): Float = ZERO_FLOAT

    @Suppress("UnusedParameter")
    public fun getDoubleOrNull(name: String): Double? = NULL_DOUBLE

    @Suppress("UnusedParameter")
    public fun getDoubleOrDefault(name: String): Double = ZERO_DOUBLE

    @Suppress("UnusedParameter")
    public fun getStringOrNull(name: String): String? = NULL_STRING

    @Suppress("UnusedParameter")
    public fun getStringOrDefault(name: String): String = EMPTY
}

public val Kick.Companion.configuration: ConfigurationAccessor
    get() = ConfigurationAccessor
