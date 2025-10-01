package ru.bartwell.kick.module.configuration.feature.presentation

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import ru.bartwell.kick.module.configuration.data.ConfigurationItem
import ru.bartwell.kick.module.configuration.data.ValueType

internal class FakeConfigurationComponent(
    items: List<ConfigurationItem>,
    initialValues: Map<String, ValueType> = emptyMap(),
) : ConfigurationComponent {
    private val _model = MutableValue(ConfigurationState(items = items, values = initialValues))
    override val model: Value<ConfigurationState> = _model

    var backInvoked = false
        private set
    var saveInvoked = false
        private set

    override fun onBackPressed() { backInvoked = true }
    override fun onSavePressed() { saveInvoked = true }
    override fun onValueChange(name: String, value: ValueType) {
        _model.value = _model.value.copy(values = _model.value.values.toMutableMap().apply { put(name, value) })
    }
}
