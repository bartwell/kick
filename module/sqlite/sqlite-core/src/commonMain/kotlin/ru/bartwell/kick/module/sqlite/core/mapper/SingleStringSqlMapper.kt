package ru.bartwell.kick.module.sqlite.core.mapper

import ru.bartwell.kick.module.sqlite.core.data.Column

public class SingleStringSqlMapper(private val column: Column) : SqlMapper<String> {
    override fun map(cursor: CursorWrapper<*>): String? {
        return cursor.getStringOrNull(column, 0)
    }
}
