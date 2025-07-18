package ru.bartwell.kick.core.component

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.bartwell.kick.core.data.ModuleDescription

@Serializable
@SerialName("StubConfig")
public data class StubConfig(val moduleDescription: ModuleDescription) : Config
