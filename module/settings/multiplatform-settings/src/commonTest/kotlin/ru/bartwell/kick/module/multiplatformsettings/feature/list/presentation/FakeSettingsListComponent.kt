package ru.bartwell.kick.module.multiplatformsettings.feature.list.presentation

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value

internal class FakeSettingsListComponent(storages: List<String>) : SettingsListComponent {
    private val _model = MutableValue(SettingsListState(storages))
    override val model: Value<SettingsListState> get() = _model

    var backInvoked = false
        private set
    var clicked: String? = null
        private set

    override fun onBackPressed() { backInvoked = true }
    override fun onItemClick(storageName: String) { clicked = storageName }
}
