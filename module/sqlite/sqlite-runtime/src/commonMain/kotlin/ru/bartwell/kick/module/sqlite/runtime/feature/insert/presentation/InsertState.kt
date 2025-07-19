package ru.bartwell.kick.module.sqlite.runtime.feature.insert.presentation

import ru.bartwell.kick.module.sqlite.core.data.Column
import ru.bartwell.kick.module.sqlite.runtime.feature.insert.data.InsertValueType

public data class InsertState(
    val table: String,
    val columns: List<Column>,
    val values: Map<Column, String> = emptyMap(),
    val valueTypes: Map<Column, InsertValueType> = emptyMap(),
    val insertError: String? = null,
)

internal fun Map<Column, InsertValueType>.getOrDefault(column: Column) = this[column] ?: InsertValueType.entries[0]
