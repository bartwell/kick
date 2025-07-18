package ru.bartwell.kick.runtime.feature.list.presentation

import com.arkivanov.decompose.value.Value
import ru.bartwell.kick.core.component.Component
import ru.bartwell.kick.core.data.ModuleDescription

internal interface ModulesListComponent : Component {
    val model: Value<ModulesListState>

    fun onListItemClicked(module: ModuleDescription)
}
