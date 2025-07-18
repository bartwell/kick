package ru.bartwell.kick.runtime.feature.list.data

import ru.bartwell.kick.core.component.StubConfig
import ru.bartwell.kick.core.data.Module
import ru.bartwell.kick.core.data.ModuleDescription

internal fun List<Module>.toModuleInfoList(): List<ModuleInfo> {
    return ModuleDescription.entries.map { description ->
        val instance = this.firstOrNull { module -> module.description == description }
        val isInitialized = instance != null
        val isStub = instance != null && instance.startConfig is StubConfig
        val isEnabled = isInitialized && !isStub
        ModuleInfo(
            isEnabled = isEnabled,
            moduleDescription = description,
        )
    }
}