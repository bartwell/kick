package ru.bartwell.kick.feature.viewer.presentation

import ru.bartwell.kick.core.data.Column
import ru.bartwell.kick.core.data.Row

public data class ViewerState(
    val table: String,
    val columns: List<Column> = emptyList(),
    val rows: List<Row> = emptyList(),
    val isDeleteMode: Boolean = false,
    val selectedRows: List<Long> = emptyList(),
    val deleteError: String? = null,
    val loadError: String? = null,
)
