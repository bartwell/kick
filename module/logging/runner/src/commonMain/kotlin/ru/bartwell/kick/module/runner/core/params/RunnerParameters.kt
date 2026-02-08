package ru.bartwell.kick.module.runner.core.params

public class RunnerParameters internal constructor(
    private val values: Map<String, Any?>,
) {
    @Suppress("UNCHECKED_CAST")
    public fun <T> get(id: String): T? = values[id] as? T

    internal companion object {
        val EMPTY: RunnerParameters = RunnerParameters(emptyMap())
    }
}
