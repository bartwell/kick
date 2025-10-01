package ru.bartwell.kick.module.controlpanel.feature.presentation

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import ru.bartwell.kick.module.controlpanel.data.ControlPanelItem
import ru.bartwell.kick.module.controlpanel.data.InputType

internal class FakeControlPanelComponent(
    items: List<ControlPanelItem>,
    initialValues: Map<String, InputType> = emptyMap(),
) : ControlPanelComponent {
    private val _model = MutableValue(ControlPanelState(items = items, values = initialValues))
    override val model: Value<ControlPanelState> = _model

    var backInvoked = false
        private set
    var saveInvoked = false
        private set

    override fun onBackPressed() { backInvoked = true }
    override fun onSavePressed() { saveInvoked = true }
    override fun onValueChange(name: String, value: InputType) {
        _model.value = _model.value.copy(values = _model.value.values.toMutableMap().apply { put(name, value) })
    }
    override fun onCategoryToggle(category: String) {
        val current = _model.value.expanded[category] ?: true
        val updated = _model.value.expanded.toMutableMap().apply { put(category, !current) }
        _model.value = _model.value.copy(expanded = updated)
    }
}
