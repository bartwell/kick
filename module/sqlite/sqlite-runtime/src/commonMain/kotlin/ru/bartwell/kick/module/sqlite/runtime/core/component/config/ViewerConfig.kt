package ru.bartwell.kick.module.sqlite.runtime.core.component.config

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.bartwell.kick.core.component.Config

@Serializable
@SerialName("ViewerConfig")
public data class ViewerConfig(val table: String) : Config
