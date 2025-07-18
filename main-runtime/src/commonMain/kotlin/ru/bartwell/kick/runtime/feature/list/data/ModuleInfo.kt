package ru.bartwell.kick.runtime.feature.list.data

import ru.bartwell.kick.core.data.ModuleDescription

internal data class ModuleInfo(
    val isEnabled: Boolean,
    val moduleDescription: ModuleDescription,
)
