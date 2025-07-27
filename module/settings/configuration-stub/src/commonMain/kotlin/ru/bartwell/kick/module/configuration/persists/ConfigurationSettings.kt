package ru.bartwell.kick.module.configuration.persists

import ru.bartwell.kick.module.configuration.data.ConfigurationItem
import ru.bartwell.kick.module.configuration.data.ValueType

@Suppress("FunctionOnlyReturningConstant", "UnusedParameter", "EmptyFunctionBlock", "unused")
internal object ConfigurationSettings {
    private var defaults = emptyMap<String, ValueType>()

    operator fun invoke(configuration: List<ConfigurationItem>) {
        defaults = configuration.associate { it.name to it.default }
    }

    inline fun <reified V : ValueType> put(key: String, value: V) {}

    inline fun <reified V : ValueType> get(key: String): V {
        return when (V::class) {
            ValueType.Boolean::class -> ValueType.Boolean(getDefault<ValueType.Boolean>(key).value)
            ValueType.Int::class -> ValueType.Int(getDefault<ValueType.Int>(key).value)
            ValueType.Long::class -> ValueType.Long(getDefault<ValueType.Long>(key).value)
            ValueType.Float::class -> ValueType.Float(getDefault<ValueType.Float>(key).value)
            ValueType.Double::class -> ValueType.Double(getDefault<ValueType.Double>(key).value)
            ValueType.String::class -> ValueType.String(getDefault<ValueType.String>(key).value)
            else -> error("Unsupported type: ${V::class.simpleName}")
        } as V
    }

    inline fun <reified V : ValueType> getOrNull(key: String): V? = null

    private inline fun <reified T : ValueType> getDefault(key: String): T {
        val default = defaults[key]
            ?: error("Key \"$key\" is not defined in configuration")
        return default as? T
            ?: error("Requested ${T::class.simpleName} but default is ${default::class.simpleName}")
    }
}
