package ru.bartwell.kick.module.runner.core.data

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.bartwell.kick.module.runner.core.params.RunnerParameter
import ru.bartwell.kick.module.runner.core.params.RunnerParameters

internal interface RunnerCallable {
    val id: String
    val title: String
    val description: String?
    val params: List<RunnerParameter<*>>

    suspend fun execute(
        callDispatcher: CoroutineDispatcher,
        resultDispatcher: CoroutineDispatcher,
        args: RunnerParameters?,
    ): RunnerRenderer<*>
}

internal class RunnerCall<T>(
    override val id: String,
    override val title: String,
    override val description: String?,
    override val params: List<RunnerParameter<*>> = emptyList(),
    private val renderer: RunnerRenderer<T>,
    private val block: suspend (RunnerParameters) -> T,
) : RunnerCallable {

    override suspend fun execute(
        callDispatcher: CoroutineDispatcher,
        resultDispatcher: CoroutineDispatcher,
        args: RunnerParameters?,
    ): RunnerRenderer<*> {
        val safeArgs = args ?: RunnerParameters.EMPTY
        val result = withContext(callDispatcher) { block(safeArgs) }
        withContext(resultDispatcher) { renderer.setResult(result) }
        return renderer
    }
}

internal class RunnerCallNoArgs<T>(
    override val id: String,
    override val title: String,
    override val description: String?,
    private val renderer: RunnerRenderer<T>,
    private val block: suspend () -> T,
) : RunnerCallable {

    override val params: List<RunnerParameter<*>> = emptyList()

    override suspend fun execute(
        callDispatcher: CoroutineDispatcher,
        resultDispatcher: CoroutineDispatcher,
        args: RunnerParameters?,
    ): RunnerRenderer<*> {
        val result = withContext(callDispatcher) { block() }
        withContext(resultDispatcher) { renderer.setResult(result) }
        return renderer
    }
}
