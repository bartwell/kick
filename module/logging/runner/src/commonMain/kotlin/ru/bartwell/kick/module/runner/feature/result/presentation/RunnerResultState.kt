package ru.bartwell.kick.module.runner.feature.result.presentation

import ru.bartwell.kick.module.runner.core.data.RunnerRenderer

internal data class RunnerResultState(
    val renderer: RunnerRenderer<*>? = null,
    val error: String? = null,
)
