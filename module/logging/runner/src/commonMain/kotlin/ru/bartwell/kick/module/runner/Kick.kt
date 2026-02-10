package ru.bartwell.kick.module.runner

import ru.bartwell.kick.Kick
import ru.bartwell.kick.module.runner.core.data.RunnerCall
import ru.bartwell.kick.module.runner.core.data.RunnerCallNoArgs
import ru.bartwell.kick.module.runner.core.data.RunnerRenderer
import ru.bartwell.kick.module.runner.core.params.RunnerParameter
import ru.bartwell.kick.module.runner.core.params.RunnerParameters
import ru.bartwell.kick.module.runner.core.store.RunnerStore
import kotlin.random.Random

public val Kick.Companion.runner: RunnerAccessor
    get() = RunnerAccessor

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
            RunnerCallNoArgs(
                id = generateId(),
                title = title,
                description = description,
                renderer = renderer,
                block = block,
            )
        )
    }

    public fun <T> addCall(
        title: String,
        description: String? = null,
        params: List<RunnerParameter<*>>,
        renderer: RunnerRenderer<T>,
        block: suspend (RunnerParameters) -> T,
    ) {
        RunnerStore.add(
            RunnerCall(
                id = generateId(),
                title = title,
                description = description,
                params = params,
                renderer = renderer,
                block = block,
            )
        )
    }

    private fun generateId(): String = "runner-${randomUuid()}"
}

@Suppress("MagicNumber")
private fun randomUuid(): String {
    val bytes = Random.nextBytes(16)
    val hexChars = "0123456789abcdef"
    var hexCount = 0
    return buildString(36) {
        bytes.forEach { byte ->
            val value = byte.toInt() and 0xFF
            append(hexChars[value ushr 4])
            append(hexChars[value and 0x0F])
            hexCount += 2
            when (hexCount) {
                8, 12, 16, 20 -> append('-')
            }
        }
    }
}
