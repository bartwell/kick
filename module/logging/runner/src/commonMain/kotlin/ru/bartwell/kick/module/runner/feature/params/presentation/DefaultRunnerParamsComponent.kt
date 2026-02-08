package ru.bartwell.kick.module.runner.feature.params.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
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
            _model.value = _model.value.copy(errorMessage = "Call not found")
            return
        }

        scope.launch {
            _model.value = _model.value.copy(isSubmitting = true, errorMessage = null)
            try {
                val renderer = call.execute(
                    callDispatcher = Dispatchers.Default,
                    resultDispatcher = Dispatchers.Main,
                    args = args,
                )
                RunnerStore.setRenderer(callId, renderer)
                _model.value = _model.value.copy(isSubmitting = false)
                onReadyToShowResult(callId)
            } catch (e: CancellationException) {
                _model.value = _model.value.copy(isSubmitting = false)
                throw e
            } catch (e: Exception) {
                _model.value = _model.value.copy(
                    isSubmitting = false,
                    errorMessage = e.message ?: e.toString(),
                )
            }
        }
    }

    private fun validate(
        params: List<RunnerParameter<*>>,
        values: Map<String, Any?>,
    ): Map<String, String?> {
        val result = mutableMapOf<String, String?>()
        params.forEach { param ->
            val value = values[param.id]
            if (param.required && (value == null || (value is String && value.isBlank()))) {
                result[param.id] = "Required"
                return@forEach
            }
            when (val type = param.type) {
                is RunnerParameterType.IntType -> {
                    val v = value as? Int ?: return@forEach
                    if (type.min != null && v < type.min) result[param.id] = "Min ${type.min}"
                    if (type.max != null && v > type.max) result[param.id] = "Max ${type.max}"
                }
                is RunnerParameterType.LongType -> {
                    val v = value as? Long ?: return@forEach
                    if (type.min != null && v < type.min) result[param.id] = "Min ${type.min}"
                    if (type.max != null && v > type.max) result[param.id] = "Max ${type.max}"
                }
                is RunnerParameterType.FloatType -> {
                    val v = value as? Float ?: return@forEach
                    if (type.min != null && v < type.min) result[param.id] = "Min ${type.min}"
                    if (type.max != null && v > type.max) result[param.id] = "Max ${type.max}"
                }
                is RunnerParameterType.DoubleType -> {
                    val v = value as? Double ?: return@forEach
                    if (type.min != null && v < type.min) result[param.id] = "Min ${type.min}"
                    if (type.max != null && v > type.max) result[param.id] = "Max ${type.max}"
                }
                else -> Unit
            }
        }
        return result
    }
}
