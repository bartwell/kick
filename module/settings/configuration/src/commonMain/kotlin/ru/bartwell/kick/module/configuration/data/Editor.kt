package ru.bartwell.kick.module.configuration.data

public sealed interface Editor {
    public data class List(val options: kotlin.collections.List<ValueType>) : Editor
    public data class InputNumber(val min: kotlin.Double? = null, val max: kotlin.Double? = null) : Editor
    public data class InputString(val singleLine: kotlin.Boolean = true) : Editor
}
