package ru.bartwell.kick.module.sqlite.core.mapper

public class StringSqlMapper : SqlMapper<String> {
    override fun map(cursor: CursorWrapper<*>): String? {
        return cursor.getString(0)
    }
}
