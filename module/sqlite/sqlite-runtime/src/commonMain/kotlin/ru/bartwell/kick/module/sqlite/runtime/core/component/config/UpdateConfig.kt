package ru.bartwell.kick.core.component.config

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.bartwell.kick.core.component.Config
import ru.bartwell.kick.core.data.Column

@Serializable
@SerialName("UpdateConfig")
public data class UpdateConfig(
    val table: String,
    val column: Column,
    val rowId: Long,
) : Config
