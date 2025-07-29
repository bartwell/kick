package ru.bartwell.kick.module.explorer.feature.view.presentation

import com.arkivanov.decompose.value.Value
import ru.bartwell.kick.core.component.Component
import ru.bartwell.kick.core.data.PlatformContext

internal interface FileViewerComponent : Component {
    val model: Value<FileViewerState>
    fun init(context: PlatformContext)
    fun onBackClick()
    fun onErrorAlertDismiss()
}
