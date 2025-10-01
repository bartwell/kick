package ru.bartwell.kick.module.controlpanel.data

public sealed interface ItemType

public sealed interface InputType : ItemType {
    public data class Boolean(val value: kotlin.Boolean) : InputType
    public data class Int(val value: kotlin.Int) : InputType
    public data class Long(val value: kotlin.Long) : InputType
    public data class Float(val value: kotlin.Float) : InputType
    public data class Double(val value: kotlin.Double) : InputType
    public data class String(val value: kotlin.String) : InputType
}

public sealed interface ActionType : ItemType {
    public data class Button(val id: kotlin.String) : ActionType
}
