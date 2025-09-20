package ru.bartwell.kick.module.layout.feature.settings.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.module.layout.core.persists.LayoutSettings

internal class DefaultLayoutComponent(
    componentContext: ComponentContext,
    private val onEnabledChangeCallback: (PlatformContext, Boolean) -> Unit,
    private val onBackCallback: () -> Unit,
) : LayoutComponent, ComponentContext by componentContext {

    private val _model = MutableValue(LayoutState())
    override val model: Value<LayoutState> = _model

    override fun init(context: PlatformContext) {
        LayoutSettings(context)
        val enabled = LayoutSettings.isEnabled()
        _model.value = LayoutState(enabled)
        onEnabledChangeCallback(context, enabled)
    }

    override fun onBackClick() = onBackCallback()

    override fun onEnabledChange(context: PlatformContext, enabled: Boolean) {
        _model.value = _model.value.copy(enabled = enabled)
        LayoutSettings.setEnabled(enabled)
        onEnabledChangeCallback(context, enabled)
    }
}
