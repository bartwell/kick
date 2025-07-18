package ru.bartwell.kick.module.sqlite.adapter.room

import androidx.sqlite.SQLiteStatement
import ru.bartwell.kick.module.sqlite.core.mapper.CursorWrapper

internal class RoomCursorWrapper(override val value: SQLiteStatement) : CursorWrapper<SQLiteStatement> {
    override fun getString(index: Int): String? = value.getTextOrNull(index)
    override fun getBoolean(index: Int): Boolean? = value.getBooleanOrNull(index)
    override fun getLong(index: Int): Long? = value.getLongOrNull(index)
    override fun getDouble(index: Int): Double? = value.getDoubleOrNull(index)
    override fun getBytes(index: Int): ByteArray? = value.getBlobOrNull(index)
}

private fun SQLiteStatement.getTextOrNull(index: Int): String? = getNullable(index) { getText(index) }
private fun SQLiteStatement.getBooleanOrNull(index: Int): Boolean? = getNullable(index) { getBoolean(index) }
private fun SQLiteStatement.getLongOrNull(index: Int): Long? = getNullable(index) { getLong(index) }
private fun SQLiteStatement.getDoubleOrNull(index: Int): Double? = getNullable(index) { getDouble(index) }
private fun SQLiteStatement.getBlobOrNull(index: Int): ByteArray? = getNullable(index) { getBlob(index) }

private fun <T> SQLiteStatement.getNullable(index: Int, block: (Int) -> T): T? {
    if (isNull(index)) return null
    @Suppress("SwallowedException", "TooGenericExceptionCaught")
    return try {
        block(index)
    } catch (e: NullPointerException) {
        null
    }
}
