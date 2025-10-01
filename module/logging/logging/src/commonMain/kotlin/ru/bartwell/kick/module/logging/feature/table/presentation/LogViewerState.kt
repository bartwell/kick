package ru.bartwell.kick.module.logging.feature.table.presentation

import ru.bartwell.kick.module.logging.core.persist.LogEntity

public data class LogViewerState(
    val log: List<LogEntity> = emptyList(),
    val error: String? = null,
    val filterQuery: String = "",
    val isFilterActive: Boolean = false,
    val isFilterDialogVisible: Boolean = false,
    val labels: List<String> = emptyList(),
    val selectedLabels: Set<String> = emptySet(),
)
