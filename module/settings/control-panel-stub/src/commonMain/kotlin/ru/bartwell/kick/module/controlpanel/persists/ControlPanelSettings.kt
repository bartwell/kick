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

    inline fun <reified V : InputType> getOrNull(key: String): V? = null
}
