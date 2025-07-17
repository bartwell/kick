package ru.bartwell.kick.runtime.feature.table.presentation

import ru.bartwell.kick.core.data.ModuleDescription

internal data class ModulesListState(
    val modules: List<ModuleDescription> = ModuleDescription.entries
)
