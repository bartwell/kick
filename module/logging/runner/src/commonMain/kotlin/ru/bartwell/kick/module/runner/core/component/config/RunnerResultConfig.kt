package ru.bartwell.kick.module.runner.core.component.config

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.bartwell.kick.core.component.Config

@Serializable
@SerialName("RunnerResult")
public data class RunnerResultConfig(val callId: String) : Config
