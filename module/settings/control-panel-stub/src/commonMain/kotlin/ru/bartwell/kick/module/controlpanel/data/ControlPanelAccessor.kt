package ru.bartwell.kick.module.controlpanel.data

import ru.bartwell.kick.module.controlpanel.core.actions.ControlPanelActions
import ru.bartwell.kick.module.controlpanel.persists.ControlPanelSettings

@Suppress("TooManyFunctions")
public class ControlPanelAccessor internal constructor() {

    public fun getBoolean(key: String): Boolean = ControlPanelSettings.get<InputType.Boolean>(key).value
    public fun getBooleanOrNull(key: String): Boolean? = ControlPanelSettings.getOrNull<InputType.Boolean>(key)?.value

    public fun getInt(key: String): Int = ControlPanelSettings.get<InputType.Int>(key).value
    public fun getIntOrNull(key: String): Int? = ControlPanelSettings.getOrNull<InputType.Int>(key)?.value

    public fun getLong(key: String): Long = ControlPanelSettings.get<InputType.Long>(key).value
    public fun getLongOrNull(key: String): Long? = ControlPanelSettings.getOrNull<InputType.Long>(key)?.value

    public fun getFloat(key: String): Float = ControlPanelSettings.get<InputType.Float>(key).value
    public fun getFloatOrNull(key: String): Float? = ControlPanelSettings.getOrNull<InputType.Float>(key)?.value

    public fun getDouble(key: String): Double = ControlPanelSettings.get<InputType.Double>(key).value
    public fun getDoubleOrNull(key: String): Double? = ControlPanelSettings.getOrNull<InputType.Double>(key)?.value

    public fun getString(key: String): String = ControlPanelSettings.get<InputType.String>(key).value
    public fun getStringOrNull(key: String): String? = ControlPanelSettings.getOrNull<InputType.String>(key)?.value

    public fun onButtonClick(listener: (id: String) -> Unit) {
        ControlPanelActions.onButtonClick = listener
    }
}
