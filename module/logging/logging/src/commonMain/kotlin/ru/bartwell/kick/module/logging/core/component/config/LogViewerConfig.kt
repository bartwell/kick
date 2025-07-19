package ru.bartwell.kick.module.logging.core.component.config

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.bartwell.kick.core.component.Config

@Serializable
@SerialName("LogViewer")
public data object LogViewerConfig : Config
