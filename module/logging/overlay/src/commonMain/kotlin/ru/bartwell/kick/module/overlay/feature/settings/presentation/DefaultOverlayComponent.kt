package ru.bartwell.kick.module.overlay.feature.settings.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.module.overlay.core.persists.OverlaySettings
import ru.bartwell.kick.module.overlay.core.store.OverlayStore

internal class DefaultOverlayComponent(
    componentContext: ComponentContext,
    private val onEnabledChangeCallback: (Boolean) -> Unit,
    private val onBackCallback: () -> Unit,
) : OverlayComponent, ComponentContext by componentContext {

    private val _model = MutableValue(OverlayState())
    override val model: Value<OverlayState> = _model

    override fun init(context: PlatformContext) {
        OverlaySettings(context)
        val enabled = OverlaySettings.isEnabled()
        val category = OverlaySettings.getSelectedCategory()
        _model.value = OverlayState(enabled)
        onEnabledChangeCallback(enabled)
        OverlayStore.selectCategory(category)
    }

    override fun onBackClick() = onBackCallback()

    override fun onEnabledChange(context: PlatformContext, enabled: Boolean) {
        _model.value = _model.value.copy(enabled = enabled)
        OverlaySettings.setEnabled(enabled)
        onEnabledChangeCallback(enabled)
    }

    override fun onCategoryChange(category: String) {
        OverlaySettings.setSelectedCategory(category)
        OverlayStore.selectCategory(category)
    }
}
