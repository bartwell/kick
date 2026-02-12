package ru.bartwell.kick.module.runner.core.store

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import ru.bartwell.kick.module.runner.core.data.RunnerCallable
import ru.bartwell.kick.module.runner.core.data.RunnerRenderer
import ru.bartwell.kick.module.runner.core.params.RunnerParameter

internal object RunnerStore {
    private val _calls = MutableStateFlow<List<RunnerCallable>>(emptyList())
    val calls: StateFlow<List<RunnerCallable>> = _calls.asStateFlow()

    private val _renderers = MutableStateFlow<Map<String, RunnerRenderer<*>>>(emptyMap())
    val renderers: StateFlow<Map<String, RunnerRenderer<*>>> = _renderers.asStateFlow()

    fun add(call: RunnerCallable) {
        _calls.update { it + call }
    }

    fun clear() {
        _calls.value = emptyList()
        _renderers.value = emptyMap()
    }

    fun get(callId: String): RunnerCallable? = _calls.value.firstOrNull { it.id == callId }

    fun setRenderer(callId: String, renderer: RunnerRenderer<*>) {
        _renderers.update { it + (callId to renderer) }
    }

    fun getRenderer(callId: String): RunnerRenderer<*>? = _renderers.value[callId]

    fun getParams(callId: String): List<RunnerParameter<*>> =
        _calls.value.firstOrNull { it.id == callId }?.params.orEmpty()
}
