package ru.bartwell.kick.module.layout.core.component.config

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.bartwell.kick.core.component.Config
import ru.bartwell.kick.module.layout.core.data.LayoutNodeId

@Serializable
@SerialName("LayoutPropertiesConfig")
internal data class LayoutPropertiesConfig(val nodeId: LayoutNodeId) : Config
