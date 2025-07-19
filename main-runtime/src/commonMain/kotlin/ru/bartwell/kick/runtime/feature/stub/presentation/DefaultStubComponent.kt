package ru.bartwell.kick.runtime.feature.stub.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import ru.bartwell.kick.core.data.ModuleDescription

internal class DefaultStubComponent(
    componentContext: ComponentContext,
    private val onFinished: () -> Unit,
    moduleDescription: ModuleDescription,
) : StubComponent, ComponentContext by componentContext {

    private val _model = MutableValue(StubState(moduleDescription = moduleDescription))
    override val model: Value<StubState> = _model

    override fun onBackPressed() = onFinished()
}
