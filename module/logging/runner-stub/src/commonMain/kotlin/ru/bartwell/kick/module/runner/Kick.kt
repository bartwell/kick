package ru.bartwell.kick.module.runner

import ru.bartwell.kick.Kick
import ru.bartwell.kick.module.runner.core.data.RunnerRenderer
import ru.bartwell.kick.module.runner.core.params.RunnerParameter
import ru.bartwell.kick.module.runner.core.params.RunnerParameters

public val Kick.Companion.runner: RunnerAccessor
    get() = RunnerAccessor

@Suppress("UnusedParameter", "EmptyFunctionBlock")
public object RunnerAccessor {
    public fun clear() {}
    public fun <T> addCall(
        title: String,
        description: String? = null,
        renderer: RunnerRenderer<T>,
        block: suspend () -> T
    ) {}

    public fun <T> addCall(
        title: String,
        description: String? = null,
        params: List<RunnerParameter<*>>,
        renderer: RunnerRenderer<T>,
        block: suspend (RunnerParameters) -> T,
    ) {}
}
