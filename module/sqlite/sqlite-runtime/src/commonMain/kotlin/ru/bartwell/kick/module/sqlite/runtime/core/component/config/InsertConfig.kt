package ru.bartwell.kick.module.sqlite.runtime.core.component.config

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.bartwell.kick.core.component.Config
import ru.bartwell.kick.module.sqlite.core.data.Column

@Serializable
@SerialName("InsertConfig")
public data class InsertConfig(val table: String, val columns: List<Column>) : Config
