package ru.bartwell.kick.module.runner.feature.params.presentation

import com.arkivanov.decompose.value.Value
import ru.bartwell.kick.core.component.Component

internal interface RunnerParamsComponent : Component {
    val model: Value<RunnerParamsState>

    fun onBackPressed()
    fun onValueChange(id: String, value: Any?)
    fun onSubmit()
}
