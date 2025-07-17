package ru.bartwell.kick.core.mapper

import ru.bartwell.kick.core.data.Column
import ru.bartwell.kick.core.data.ColumnType

public interface CursorWrapper<T> {
    public val value: T
    public fun getString(index: Int): String?
    public fun getBoolean(index: Int): Boolean?
    public fun getLong(index: Int): Long?
    public fun getDouble(index: Int): Double?
    public fun getBytes(index: Int): ByteArray?

    @OptIn(ExperimentalStdlibApi::class)
    public fun getStringOrNull(column: Column, index: Int): String? = when (column.type) {
        ColumnType.INTEGER -> getLong(index)?.toString()
        ColumnType.TEXT -> getString(index)
        ColumnType.REAL -> getDouble(index)?.toString()
        ColumnType.BLOB -> getBytes(index)?.toHexString()
    }
}
