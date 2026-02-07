package ru.bartwell.kick.module.runner.feature.list.presentation

import com.arkivanov.decompose.value.Value
import ru.bartwell.kick.core.component.Component

internal interface RunnerListComponent : Component {
    val model: Value<RunnerListState>

    fun onBackPressed()
    fun onCallClick(callId: String)
}
