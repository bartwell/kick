package ru.bartwell.kick.module.controlpanel.feature.presentation

import com.arkivanov.decompose.value.Value
import ru.bartwell.kick.core.component.Component
import ru.bartwell.kick.module.controlpanel.data.InputType

internal interface ControlPanelComponent : Component {
    val model: Value<ControlPanelState>
    fun onBackPressed()
    fun onSavePressed()
    fun onValueChange(name: String, value: InputType)
    fun onCategoryToggle(category: String)
}
