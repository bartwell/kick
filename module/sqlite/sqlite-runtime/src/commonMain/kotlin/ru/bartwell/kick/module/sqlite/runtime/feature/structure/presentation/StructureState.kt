package ru.bartwell.kick.feature.structure.presentation

import ru.bartwell.kick.core.data.Column

public data class StructureState(
    val table: String,
    val columns: List<Column> = emptyList(),
    val error: String? = null,
)
