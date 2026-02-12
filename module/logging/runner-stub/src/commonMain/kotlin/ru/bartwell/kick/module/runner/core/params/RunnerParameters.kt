package ru.bartwell.kick.module.runner.core.params

@Suppress("UnusedPrivateProperty")
public class RunnerParameters internal constructor(
    private val values: Map<String, Any?>,
) {
    @Suppress("UnusedParameter", "FunctionOnlyReturningConstant")
    public fun <T> get(id: String): T? = null

    internal companion object {
        val EMPTY: RunnerParameters = RunnerParameters(emptyMap())
    }
}
