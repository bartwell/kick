package ru.bartwell.kick.module.overlay.feature.settings.presentation

import com.arkivanov.decompose.value.Value
import ru.bartwell.kick.core.component.Component
import ru.bartwell.kick.core.data.PlatformContext

internal interface OverlayComponent : Component {
    val model: Value<OverlayState>

    fun init(context: PlatformContext)
    fun onBackClick()
    fun onEnabledChange(context: PlatformContext, enabled: Boolean)
}

internal data class OverlayState(
    val enabled: Boolean = false,
)

