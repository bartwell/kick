package ru.bartwell.kick.module.overlay.feature.settings.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.core.util.PlatformUtils
import ru.bartwell.kick.module.overlay.core.persists.OverlaySettings
import ru.bartwell.kick.module.overlay.core.provider.OverlayProvider
import ru.bartwell.kick.module.overlay.core.store.OverlayStore
import ru.bartwell.kick.module.overlay.feature.settings.data.ProviderDescription

internal class DefaultOverlayComponent(
    componentContext: ComponentContext,
    private val onEnabledChangeCallback: (Boolean) -> Unit,
    private val onBackCallback: () -> Unit,
    providers: List<OverlayProvider>,
) : OverlayComponent, ComponentContext by componentContext {

    private val _model = MutableValue(OverlayState(providers = providers.toDescriptions()))
    override val model: Value<OverlayState> = _model

    override fun init(context: PlatformContext) {
        OverlaySettings(context)
        val enabled = OverlaySettings.isEnabled()
        val category = OverlaySettings.getSelectedCategory()
        _model.value = model.value.copy(enabled = enabled)
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
        val unavailableProviders = model.value.providers.filter { it.categories.contains(category) && !it.isAvailable }
        if (unavailableProviders.isEmpty()) {
            _model.value = model.value.copy(warning = null)
        } else {
            val platform = PlatformUtils.getPlatform().name
            val providersText = unavailableProviders.joinToString(", ") { it.name }
            _model.value = model.value.copy(warning = "$providersText is not available on $platform")
        }
    }
}

private fun List<OverlayProvider>.toDescriptions(): List<ProviderDescription> = map { provider ->
    ProviderDescription(
        name = provider::class.simpleName ?: "Unknown",
        categories = provider.categories,
        isAvailable = provider.isAvailable
    )
}
