package ru.bartwell.kick.module.sqlite.core.data

import kotlinx.serialization.Serializable

private const val ROW_ID_COLUMN_NAME = "rowid"

@Serializable
public data class Column(
    val name: String,
    val type: ColumnType,
    val isNotNullable: Boolean,
    val defaultValue: String?,
) {
    val isRowId: Boolean
        get() = name == ROW_ID_COLUMN_NAME

    public companion object {
        public val ROW_ID_COLUMN: Column = Column(
            name = ROW_ID_COLUMN_NAME,
            type = ColumnType.INTEGER,
            isNotNullable = true,
            defaultValue = "",
        )
    }
}
