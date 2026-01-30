package ru.bartwell.kick.module.controlpanel.feature.main.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import ru.bartwell.kick.module.controlpanel.core.actions.ControlPanelActions
import ru.bartwell.kick.module.controlpanel.core.persists.ControlPanelSettings
import ru.bartwell.kick.module.controlpanel.data.ActionType
import ru.bartwell.kick.module.controlpanel.data.ControlPanelEvent
import ru.bartwell.kick.module.controlpanel.data.ControlPanelItem
import ru.bartwell.kick.module.controlpanel.data.InputType

internal class DefaultControlPanelComponent(
    componentContext: ComponentContext,
    private val items: List<ControlPanelItem>,
    private val onFinished: () -> Unit,
) : ControlPanelComponent, ComponentContext by componentContext {

    private val _model = MutableValue(ControlPanelState(items, loadValues(), expanded = emptyMap()))
    override val model: Value<ControlPanelState> = _model

    override fun onBackPressed() {
        ControlPanelActions.emitEvent(ControlPanelEvent.ModuleExited)
        onFinished()
    }

    override fun onSavePressed() {
        ControlPanelActions.emitEvent(ControlPanelEvent.SaveClicked)
        for ((name, value) in model.value.values) {
            ControlPanelSettings.put(name, value)
        }
        _model.value = model.value.copy(values = loadValues())
    }

    override fun onValueChange(name: String, value: InputType) {
        ControlPanelActions.emitEvent(ControlPanelEvent.ValueChanged(name, value))
        val map = model.value.values.toMutableMap()
        map[name] = value
        _model.value = model.value.copy(values = map)
    }

    override fun onCategoryToggle(category: String) {
        val current = model.value.expanded[category] ?: true
        val updated = model.value.expanded.toMutableMap().apply { put(category, !current) }
        _model.value = model.value.copy(expanded = updated)
    }

    override fun onActionButtonClick(id: String) {
        ControlPanelActions.emitEvent(ControlPanelEvent.ButtonClicked(id))
    }

    private fun loadValues(): Map<String, InputType> =
        items.mapNotNull { item ->
            val name = item.name
            when (item.type) {
                is InputType.Boolean -> name to ControlPanelSettings.get<InputType.Boolean>(name)
                is InputType.Int -> name to ControlPanelSettings.get<InputType.Int>(name)
                is InputType.Long -> name to ControlPanelSettings.get<InputType.Long>(name)
                is InputType.Float -> name to ControlPanelSettings.get<InputType.Float>(name)
                is InputType.Double -> name to ControlPanelSettings.get<InputType.Double>(name)
                is InputType.String -> name to ControlPanelSettings.get<InputType.String>(name)
                is ActionType.Button -> null
            }
        }.toMap()
}
