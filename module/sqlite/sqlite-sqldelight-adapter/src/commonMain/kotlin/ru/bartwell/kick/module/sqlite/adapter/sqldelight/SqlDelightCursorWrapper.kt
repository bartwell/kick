package ru.bartwell.kick.module.sqlite.adapter.sqldelight

import app.cash.sqldelight.db.SqlCursor
import ru.bartwell.kick.core.mapper.CursorWrapper

internal class SqlDelightCursorWrapper(override val value: SqlCursor) : CursorWrapper<SqlCursor> {
    override fun getString(index: Int): String? = value.getString(index)
    override fun getBoolean(index: Int): Boolean? = value.getBoolean(index)
    override fun getLong(index: Int): Long? = value.getLong(index)
    override fun getDouble(index: Int): Double? = value.getDouble(index)
    override fun getBytes(index: Int): ByteArray? = value.getBytes(index)
}
