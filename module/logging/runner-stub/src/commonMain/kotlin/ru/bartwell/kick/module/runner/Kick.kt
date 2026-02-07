package ru.bartwell.kick.module.runner

import ru.bartwell.kick.Kick
import ru.bartwell.kick.module.runner.core.data.RunnerRenderer

public val Kick.Companion.runner: RunnerAccessor
    get() = RunnerAccessor

@Suppress("TooManyFunctions", "UnusedParameter", "EmptyFunctionBlock")
public object RunnerAccessor {
    public fun clear() {}
    public fun <T> addCall(title: String, description: String? = null, renderer: RunnerRenderer<T>, block: suspend () -> T) {}
}
