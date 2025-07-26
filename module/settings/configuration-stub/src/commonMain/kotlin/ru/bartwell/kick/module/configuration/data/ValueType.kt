package ru.bartwell.kick.module.configuration.data

public sealed interface ValueType {
    public data class Bool(val value: kotlin.Boolean) : ValueType
    public data class Int(val value: kotlin.Int) : ValueType
    public data class Long(val value: kotlin.Long) : ValueType
    public data class Float(val value: kotlin.Float) : ValueType
    public data class Double(val value: kotlin.Double) : ValueType
    public data class Str(val value: kotlin.String) : ValueType
}
