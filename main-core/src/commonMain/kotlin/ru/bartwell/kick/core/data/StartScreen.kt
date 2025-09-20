package ru.bartwell.kick.core.data

import kotlinx.serialization.Serializable
import ru.bartwell.kick.core.component.Config

@Serializable
public data class StartScreen(
    val config: Config,
    val moduleDescription: ModuleDescription,
)
