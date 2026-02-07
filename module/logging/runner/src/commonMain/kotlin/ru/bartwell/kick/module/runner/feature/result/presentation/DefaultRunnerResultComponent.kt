package ru.bartwell.kick.module.runner.feature.result.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.bartwell.kick.module.runner.core.store.RunnerStore

internal class DefaultRunnerResultComponent(
    componentContext: ComponentContext,
    private val callId: String,
    private val onFinished: () -> Unit,
) : RunnerResultComponent, ComponentContext by componentContext {

    private val _model = MutableValue(RunnerResultState())
    override val model: Value<RunnerResultState> = _model

    init {
        RunnerStore.renderers
            .onEach { renderers ->
                val renderer = renderers[callId]
                if (renderer != null) {
                    _model.value = RunnerResultState(renderer = renderer)
                }
            }
            .launchIn(coroutineScope())

        // If renderer already exists in store before subscription
        RunnerStore.getRenderer(callId)?.let { renderer ->
            _model.value = RunnerResultState(renderer = renderer)
        }
    }

    override fun onBackPressed() {
        onFinished()
    }
}
