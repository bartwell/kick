package ru.bartwell.kick.module.runner.core.data

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

internal interface RunnerCallable {
    val id: String
    val title: String
    val description: String?
    suspend fun execute(callDispatcher: CoroutineDispatcher, resultDispatcher: CoroutineDispatcher): RunnerRenderer<*>
}

internal class RunnerCall<T>(
    override val id: String,
    override val title: String,
    override val description: String?,
    private val renderer: RunnerRenderer<T>,
    private val block: suspend () -> T,
) : RunnerCallable {

    override suspend fun execute(
        callDispatcher: CoroutineDispatcher,
        resultDispatcher: CoroutineDispatcher,
    ): RunnerRenderer<*> {
        val result = withContext(callDispatcher) { block() }
        withContext(resultDispatcher) { renderer.setResult(result) }
        return renderer
    }
}
