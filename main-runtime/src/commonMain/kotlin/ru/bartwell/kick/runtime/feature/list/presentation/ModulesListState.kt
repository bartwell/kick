package ru.bartwell.kick.runtime.feature.list.presentation

import ru.bartwell.kick.runtime.feature.list.data.ModuleInfo

internal data class ModulesListState(
    val modules: List<ModuleInfo>,
    val showAll: Boolean = false,
) {
    val modulesToShow: List<ModuleInfo>
        get() = if (showAll) modules else modules.filter { it.isEnabled }
    val isAllModulesEnabled: Boolean
        get() = modules.all { it.isEnabled }
}
