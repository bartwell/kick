package ru.bartwell.kick.module.controlpanel.persists

import ru.bartwell.kick.module.controlpanel.data.ControlPanelItem
import ru.bartwell.kick.module.controlpanel.data.InputType

@Suppress("FunctionOnlyReturningConstant", "UnusedParameter", "EmptyFunctionBlock", "unused")
internal object ControlPanelSettings {
    private var defaults = emptyMap<String, InputType>()

    operator fun invoke(configuration: List<ControlPanelItem>) {
        defaults = configuration.mapNotNull { item ->
            val t = item.type
            if (t is InputType) item.name to t else null
        }.toMap()
    }

    inline fun <reified V : InputType> put(key: String, value: V) {}

    inline fun <reified V : InputType> get(key: String): V {
        return when (V::class) {
            InputType.Boolean::class -> InputType.Boolean(getDefault<InputType.Boolean>(key).value)
            InputType.Int::class -> InputType.Int(getDefault<InputType.Int>(key).value)
            InputType.Long::class -> InputType.Long(getDefault<InputType.Long>(key).value)
            InputType.Float::class -> InputType.Float(getDefault<InputType.Float>(key).value)
            InputType.Double::class -> InputType.Double(getDefault<InputType.Double>(key).value)
            InputType.String::class -> InputType.String(getDefault<InputType.String>(key).value)
            else -> error("Unsupported type: ${V::class.simpleName}")
        } as V
    }

    inline fun <reified V : InputType> getOrNull(key: String): V? = null

    private inline fun <reified T : InputType> getDefault(key: String): T {
        val default = defaults[key]
            ?: error("Key \"$key\" is not defined in configuration")
        return default as? T
            ?: error("Requested ${T::class.simpleName} but default is ${default::class.simpleName}")
    }
}
