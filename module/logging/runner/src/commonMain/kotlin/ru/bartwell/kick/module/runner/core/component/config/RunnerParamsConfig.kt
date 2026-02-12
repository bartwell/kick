package ru.bartwell.kick.module.runner.core.component.config

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.bartwell.kick.core.component.Config

@Serializable
@SerialName("RunnerParams")
public data class RunnerParamsConfig(val callId: String) : Config
