package ru.bartwell.kick.module.explorer.core.component.config

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.bartwell.kick.core.component.Config

@Serializable
@SerialName("FileOptionsConfig")
internal data class FileOptionsConfig(val path: String) : Config
