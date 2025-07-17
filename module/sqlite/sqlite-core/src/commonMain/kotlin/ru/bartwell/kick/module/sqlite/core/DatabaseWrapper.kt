package ru.bartwell.kick.core

import kotlinx.coroutines.flow.Flow
import ru.bartwell.kick.core.data.Column
import ru.bartwell.kick.core.mapper.SqlMapper

public abstract class DatabaseWrapper {
    public abstract val type: DatabaseWrapper.Type
    public abstract fun <T> query(sql: String, mapper: SqlMapper<T>): Flow<List<T>>
    public abstract fun <T> querySingle(sql: String, mapper: SqlMapper<T>): Flow<T?>
    public abstract fun updateSingle(table: String, id: Long, column: Column, value: String?): Flow<Unit>
    public abstract fun insert(table: String, values: Map<Column, String?>): Flow<Unit>
    public abstract fun delete(table: String, ids: List<Long>): Flow<Unit>
    public abstract fun raw(sql: String): Flow<Unit>

    protected fun buildUpdateQuery(table: String, column: Column): String =
        "UPDATE $table SET ${column.name} = ? WHERE rowid = ?"

    protected fun buildInsertQuery(table: String, values: Map<Column, String?>): String {
        return if (values.isEmpty()) {
            "INSERT INTO $table DEFAULT VALUES;"
        } else {
            val columnsPart = values.keys.joinToString(",") { it.name }
            val valuesPart = List(values.size) { "?" }.joinToString(",")
            "INSERT INTO $table ($columnsPart) VALUES ($valuesPart)"
        }
    }

    protected fun buildDeleteQuery(table: String, ids: List<Long>): String {
        val whereClause = "rowid=" + ids.joinToString(" OR rowid=")
        return "DELETE FROM $table WHERE $whereClause;"
    }

    public enum class Type {
        SQL_DELIGHT, ROOM
    }
}
