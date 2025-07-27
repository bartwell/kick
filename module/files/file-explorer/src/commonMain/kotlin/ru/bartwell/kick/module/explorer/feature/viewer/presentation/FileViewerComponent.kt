package ru.bartwell.kick.module.explorer.feature.viewer.presentation

import com.arkivanov.decompose.value.Value
import ru.bartwell.kick.core.component.Component

internal interface FileViewerComponent : Component {
    val model: Value<FileViewerState>
    fun onBackPressed()
}
