package ru.bartwell.kick.module.multiplatformsettings.feature.list.presentation

import com.arkivanov.decompose.value.Value
import ru.bartwell.kick.core.component.Component

public interface SettingsListComponent : Component {
    public val model: Value<SettingsListState>

    public fun onBackPressed()
    public fun onItemClick(storageName: String)
}
