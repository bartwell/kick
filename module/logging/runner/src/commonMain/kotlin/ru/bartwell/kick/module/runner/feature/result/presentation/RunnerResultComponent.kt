package ru.bartwell.kick.module.runner.feature.result.presentation

import com.arkivanov.decompose.value.Value
import ru.bartwell.kick.core.component.Component

internal interface RunnerResultComponent : Component {
    val model: Value<RunnerResultState>

    fun onBackPressed()
}
