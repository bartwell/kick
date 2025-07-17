package ru.bartwell.kick.runtime.core.domain

import ru.bartwell.kick.core.data.Module

internal interface LaunchStorage {
    var modules: List<Module>
}
