package ru.bartwell.kick.module.sqlite.runtime.feature.structure.presentation

import ru.bartwell.kick.module.sqlite.core.data.Column

public data class StructureState(
    val table: String,
    val columns: List<Column> = emptyList(),
    val error: String? = null,
)
