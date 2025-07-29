package ru.bartwell.kick.module.explorer.core.component.config

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.bartwell.kick.core.component.Config

@Serializable
@SerialName("FileViewerConfig")
internal data class FileViewerConfig(val path: String) : Config
