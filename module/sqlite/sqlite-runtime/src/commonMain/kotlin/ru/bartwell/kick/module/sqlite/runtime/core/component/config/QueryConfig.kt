package ru.bartwell.kick.module.sqlite.runtime.core.component.config

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.bartwell.kick.core.component.Config

@Serializable
@SerialName("QueryConfig")
public data object QueryConfig : Config
