package ru.bartwell.kick.runtime.feature.stub.presentation

import com.arkivanov.decompose.value.Value
import ru.bartwell.kick.core.component.Component

internal interface StubComponent : Component {
    val model: Value<StubState>

    fun onBackPressed()
}
