package ru.bartwell.kick.runtime.core.data

import kotlinx.coroutines.flow.MutableStateFlow
import ru.bartwell.kick.core.data.Module
import ru.bartwell.kick.runtime.core.domain.LaunchStorage

public class LaunchInMemoryStorage : LaunchStorage {

    private val modulesState = MutableStateFlow(emptyList<Module>())
    override var modules: List<Module> by modulesState::value
}
