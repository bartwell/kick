package ru.bartwell.kick.module.layout.feature.settings.presentation

import com.arkivanov.decompose.value.Value
import ru.bartwell.kick.core.component.Component
import ru.bartwell.kick.core.data.PlatformContext

internal interface LayoutComponent : Component {
    val model: Value<LayoutState>
    fun init(context: PlatformContext)
    fun onBackClick()
    fun onEnabledChange(context: PlatformContext, enabled: Boolean)
}
