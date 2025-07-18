package ru.bartwell.kick.runtime.feature.list.presentation

import ru.bartwell.kick.core.data.ModuleDescription

internal data class ModulesListState(
    val modules: List<ModuleDescription> = ModuleDescription.entries
)
