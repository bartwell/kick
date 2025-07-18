package ru.bartwell.kick.runtime.feature.list.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import ru.bartwell.kick.core.data.ModuleDescription

internal class DefaultModulesListComponent(
    componentContext: ComponentContext,
    private val listItemClicked: (ModuleDescription) -> Unit,
) : ModulesListComponent, ComponentContext by componentContext {

    private val _model = MutableValue(ModulesListState())
    override val model: Value<ModulesListState> = _model

    override fun onListItemClicked(module: ModuleDescription) = listItemClicked(module)
}
