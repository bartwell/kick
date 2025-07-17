package ru.bartwell.kick.feature.table.presentation

public data class TablesListState(
    val tables: List<String> = emptyList(),
    val error: String? = null,
)
