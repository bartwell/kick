package ru.bartwell.kick.module.multiplatformsettings.feature.list.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.russhwolf.settings.Settings

internal class DefaultSettingsListComponent(
    componentContext: ComponentContext,
    settingsList: List<Pair<String, Settings>>,
    private val onFinished: () -> Unit,
    private val onStorageClick: (String) -> Unit,
) : SettingsListComponent, ComponentContext by componentContext {

    private val _model = MutableValue(SettingsListState(settingsList.map { it.first }))
    override val model: Value<SettingsListState> = _model

    override fun onBackPressed() {
        onFinished()
    }

    override fun onItemClick(storageName: String) {
        onStorageClick(storageName)
    }
}
