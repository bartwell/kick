package ru.bartwell.kick.module.runner.feature.list.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ru.bartwell.kick.module.runner.core.store.RunnerStore

internal class DefaultRunnerListComponent(
    componentContext: ComponentContext,
    private val onFinished: () -> Unit,
    private val onCallReady: (String) -> Unit,
) : RunnerListComponent, ComponentContext by componentContext {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val _model = MutableValue(RunnerListState())
    override val model: Value<RunnerListState> = _model

    init {
        RunnerStore.calls
            .onEach { calls ->
                _model.value = _model.value.copy(
                    calls = calls.map { RunnerListItem(it.id, it.title, it.description) }
                )
            }
            .launchIn(coroutineScope())
    }

    override fun onBackPressed() {
        onFinished()
    }

    override fun onCallClick(callId: String) {
        if (_model.value.runningCallId != null) return

        val call = RunnerStore.get(callId)
        if (call == null) {
            _model.value = _model.value.copy(error = "Call not found")
            return
        }

        scope.launch {
            _model.value = _model.value.copy(runningCallId = callId, error = null)
            try {
                val renderer = call.execute(callDispatcher = Dispatchers.Default, resultDispatcher = Dispatchers.Main)
                RunnerStore.setRenderer(callId, renderer)
                _model.value = _model.value.copy(runningCallId = null)
                onCallReady(callId)
            } catch (e: CancellationException) {
                _model.value = _model.value.copy(runningCallId = null)
                throw e
            } catch (e: Exception) {
                _model.value = _model.value.copy(
                    runningCallId = null,
                    error = e.message ?: e.toString(),
                )
            }
        }
    }
}
