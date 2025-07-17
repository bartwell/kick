package ru.bartwell.kick.core.component.config

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.bartwell.kick.core.component.Config

@Serializable
@SerialName("ViewerConfig")
public data class ViewerConfig(val table: String) : Config
