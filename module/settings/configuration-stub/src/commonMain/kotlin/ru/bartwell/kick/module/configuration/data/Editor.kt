package ru.bartwell.kick.module.configuration.data

public sealed interface Editor {
    public data class List(val options: kotlin.collections.List<ValueType>) : Editor
    public data class InputNumber(val min: Double? = null, val max: Double? = null) : Editor
    public data class InputString(val singleLine: Boolean = true) : Editor
}
