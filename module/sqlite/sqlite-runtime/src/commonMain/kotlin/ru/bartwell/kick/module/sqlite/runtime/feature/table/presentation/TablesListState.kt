package ru.bartwell.kick.module.sqlite.runtime.feature.table.presentation

public data class TablesListState(
    val tables: List<String> = emptyList(),
    val error: String? = null,
)
