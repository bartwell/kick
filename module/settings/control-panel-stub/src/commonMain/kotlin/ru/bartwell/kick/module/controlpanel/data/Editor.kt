package ru.bartwell.kick.module.controlpanel.data

public sealed interface Editor {
    public data class List(val options: kotlin.collections.List<InputType>) : Editor
    public data class InputNumber(val min: Double? = null, val max: Double? = null) : Editor
    public data class InputString(val singleLine: Boolean = true) : Editor
}
