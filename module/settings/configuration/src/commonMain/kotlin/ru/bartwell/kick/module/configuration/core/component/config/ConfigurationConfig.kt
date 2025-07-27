package ru.bartwell.kick.module.configuration.core.component.config

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.bartwell.kick.core.component.Config

@Serializable
@SerialName("Configuration")
internal data object ConfigurationConfig : Config
