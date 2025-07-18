package ru.bartwell.kick.module.sqlite.runtime.feature.update.presentation

import ru.bartwell.kick.module.sqlite.core.data.Column

public data class UpdateState(
    val table: String,
    val column: Column,
    val rowId: Long,
    val value: String? = "",
    val isNull: Boolean = false,
    val loadError: String? = null,
    val saveError: String? = null,
)
