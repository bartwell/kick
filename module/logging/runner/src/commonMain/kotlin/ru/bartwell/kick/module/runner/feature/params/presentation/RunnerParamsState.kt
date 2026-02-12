package ru.bartwell.kick.module.runner.feature.params.presentation

import ru.bartwell.kick.module.runner.core.params.RunnerParameter

internal data class RunnerParamsState(
    val title: String = "",
    val description: String? = null,
    val params: List<RunnerParameter<*>> = emptyList(),
    val values: Map<String, Any?> = emptyMap(),
    val errors: Map<String, String?> = emptyMap(),
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
)
