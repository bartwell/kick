package ru.bartwell.kick.module.runner.feature.params.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import ru.bartwell.kick.module.runner.core.CALL_NOT_FOUND_ERROR
import ru.bartwell.kick.module.runner.core.MAX_PREFIX
import ru.bartwell.kick.module.runner.core.MIN_PREFIX
import ru.bartwell.kick.module.runner.core.REQUIRED_ERROR
import ru.bartwell.kick.module.runner.core.params.RunnerParameter
import ru.bartwell.kick.module.runner.core.params.RunnerParameterType
import ru.bartwell.kick.module.runner.core.params.RunnerParameters
import ru.bartwell.kick.module.runner.core.store.RunnerStore

internal class DefaultRunnerParamsComponent(
    componentContext: ComponentContext,
    private val callId: String,
    private val onFinished: () -> Unit,
    private val onReadyToShowResult: (String) -> Unit,
) : RunnerParamsComponent, ComponentContext by componentContext {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val _model = MutableValue(
        RunnerParamsState(
            params = RunnerStore.getParams(callId),
            title = RunnerStore.get(callId)?.title.orEmpty(),
            description = RunnerStore.get(callId)?.description,
            values = RunnerStore.getParams(callId).associate { it.id to it.defaultValue },
        )
    )
    override val model: Value<RunnerParamsState> = _model

    init {
        if (_model.value.params.isEmpty()) {
            onReadyToShowResult(callId)
        }
    }

    override fun onBackPressed() {
        onFinished()
    }

    override fun onValueChange(id: String, value: Any?) {
        val newValues = _model.value.values.toMutableMap()
        newValues[id] = value
        _model.value = _model.value.copy(values = newValues, errors = _model.value.errors - id)
    }

    override fun onSubmit() {
        if (_model.value.isSubmitting) return
        val params = _model.value.params
        val errors = validate(params, _model.value.values)
        if (errors.isNotEmpty()) {
            _model.value = _model.value.copy(errors = errors)
            return
        }
        val args = RunnerParameters(_model.value.values)

        val call = RunnerStore.get(callId)
        if (call == null) {
            _model.value = _model.value.copy(errorMessage = CALL_NOT_FOUND_ERROR)
            return
        }

        scope.launch {
            _model.value = _model.value.copy(isSubmitting = true, errorMessage = null)
            runCatching {
                call.execute(
                    callDispatcher = Dispatchers.Default,
                    resultDispatcher = Dispatchers.Main,
                    args = args,
                )
            }.onSuccess { renderer ->
                RunnerStore.setRenderer(callId, renderer)
                _model.value = _model.value.copy(isSubmitting = false)
                onReadyToShowResult(callId)
            }.onFailure { throwable ->
                _model.value = _model.value.copy(
                    isSubmitting = false,
                    errorMessage = throwable.message ?: throwable.toString(),
                )
            }
        }
    }

    private fun validate(
        params: List<RunnerParameter<*>>,
        values: Map<String, Any?>,
    ): Map<String, String?> {
        return params.mapNotNull { param ->
            val value = values[param.id]
            requiredError(param, value)
                ?: numericError(param, value)
        }.toMap()
    }

    private fun requiredError(param: RunnerParameter<*>, value: Any?): Pair<String, String?>? {
        val isMissing = param.required && (value == null || value is String && value.isBlank())
        return if (isMissing) param.id to REQUIRED_ERROR else null
    }

    private fun numericError(
        param: RunnerParameter<*>,
        value: Any?,
    ): Pair<String, String?>? {
        return when (val type = param.type) {
            is RunnerParameterType.IntType -> boundsError(param.id, value as? Int, type.min, type.max)
            is RunnerParameterType.LongType -> boundsError(param.id, value as? Long, type.min, type.max)
            is RunnerParameterType.FloatType -> boundsError(param.id, value as? Float, type.min, type.max)
            is RunnerParameterType.DoubleType -> boundsError(param.id, value as? Double, type.min, type.max)
            else -> null
        }
    }

    private fun <T : Comparable<T>> boundsError(
        id: String,
        value: T?,
        min: T?,
        max: T?,
    ): Pair<String, String?>? {
        val v = value ?: return null
        if (min != null && v < min) return id to "$MIN_PREFIX$min"
        if (max != null && v > max) return id to "$MAX_PREFIX$max"
        return null
    }
}
