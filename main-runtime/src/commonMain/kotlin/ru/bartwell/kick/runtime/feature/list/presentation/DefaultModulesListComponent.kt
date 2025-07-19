package ru.bartwell.kick.runtime.feature.list.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import ru.bartwell.kick.core.data.Module
import ru.bartwell.kick.core.data.ModuleDescription
import ru.bartwell.kick.runtime.feature.list.data.toModuleInfoList

internal class DefaultModulesListComponent(
    componentContext: ComponentContext,
    private val listItemClicked: (ModuleDescription) -> Unit,
    modules: List<Module>,
) : ModulesListComponent, ComponentContext by componentContext {

    private val _model = MutableValue(ModulesListState(modules = modules.toModuleInfoList()))
    override val model: Value<ModulesListState> = _model

    override fun onListItemClicked(module: ModuleDescription) = listItemClicked(module)

    override fun onShowAllClicked() {
        _model.value = model.value.copy(showAll = !_model.value.showAll)
    }
}
