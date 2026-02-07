package ru.bartwell.kick.module.runner

import kotlin.random.Random
import ru.bartwell.kick.Kick
import ru.bartwell.kick.core.util.DateUtils
import ru.bartwell.kick.module.runner.core.data.RunnerCall
import ru.bartwell.kick.module.runner.core.data.RunnerRenderer
import ru.bartwell.kick.module.runner.core.store.RunnerStore

public val Kick.Companion.runner: RunnerAccessor
    get() = RunnerAccessor

@Suppress("TooManyFunctions")
public object RunnerAccessor {
    public fun clear() {
        RunnerStore.clear()
    }

    public fun <T> addCall(
        title: String,
        description: String? = null,
        renderer: RunnerRenderer<T>,
        block: suspend () -> T,
    ) {
        RunnerStore.add(
            RunnerCall(
                id = generateId(),
                title = title,
                description = description,
                renderer = renderer,
                block = block,
            )
        )
    }

    private fun generateId(): String =
        "runner-${DateUtils.currentTimeMillis()}-${Random.nextInt(0, 10_000_000)}"
}
