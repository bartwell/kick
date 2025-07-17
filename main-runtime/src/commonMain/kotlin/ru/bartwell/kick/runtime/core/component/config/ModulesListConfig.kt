package ru.bartwell.kick.runtime.core.component.config

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.bartwell.kick.core.component.Config

@Serializable
@SerialName("ModulesListConfig")
internal data object ModulesListConfig : Config
