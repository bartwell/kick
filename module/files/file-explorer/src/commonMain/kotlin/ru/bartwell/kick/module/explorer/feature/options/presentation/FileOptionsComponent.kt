package ru.bartwell.kick.module.explorer.feature.options.presentation

import com.arkivanov.decompose.value.Value
import ru.bartwell.kick.core.component.Component
import ru.bartwell.kick.core.data.PlatformContext

internal interface FileOptionsComponent : Component {
    val model: Value<FileOptionsState>
    fun onDismiss()
    fun onViewAsText()
    fun onDownload(context: PlatformContext)
    fun onAlertDismiss()
}
