package ru.bartwell.kick.module.sqlite.adapter.sqldelight

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlPreparedStatement
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.bartwell.kick.module.sqlite.core.DatabaseWrapper
import ru.bartwell.kick.module.sqlite.core.data.Column
import ru.bartwell.kick.module.sqlite.core.data.ColumnType
import ru.bartwell.kick.module.sqlite.core.mapper.SqlMapper

public class SqlDelightWrapper(private val driver: SqlDriver) : DatabaseWrapper() {

    override val type: Type = Type.SQL_DELIGHT

    override fun <T> query(sql: String, mapper: SqlMapper<T>): Flow<List<T>> = flow {
        val result = driver.executeQuery(
            identifier = null,
            sql = sql,
            parameters = 0,
            mapper = { cursor ->
                QueryResult.Value(
                    buildList {
                        while (cursor.next().value) {
                            mapper.map(SqlDelightCursorWrapper(cursor))?.let { add(it) }
                        }
                    }
                )
            }
        ).value
        emit(result)
    }

    override fun <T> querySingle(sql: String, mapper: SqlMapper<T>): Flow<T?> = flow {
        val result = driver.executeQuery(
            identifier = null,
            sql = sql,
            parameters = 0,
            mapper = { cursor ->
                QueryResult.Value(
                    buildList {
                        val value = if (cursor.next().value) {
                            mapper.map(SqlDelightCursorWrapper(cursor))
                        } else {
                            null
                        }
                        add(value)
                    }
                )
            }
        )
            .value
            .first()
        emit(result)
    }

    @OptIn(ExperimentalStdlibApi::class)
    override fun updateSingle(table: String, id: Long, column: Column, value: String?): Flow<Unit> = flow {
        val sql = buildUpdateQuery(table, column)
        driver.execute(
            identifier = null,
            sql = sql,
            parameters = 2,
            binders = {
                bindValue(index = 0, column = column, value = value)
                bindLong(index = 1, long = id)
            }
        )
        emit(Unit)
    }

    @OptIn(ExperimentalStdlibApi::class)
    public override fun insert(table: String, values: Map<Column, String?>): Flow<Unit> = flow {
        val sql = buildInsertQuery(table, values)
        driver.execute(
            identifier = null,
            sql = sql,
            parameters = values.size,
            binders = {
                if (values.isNotEmpty()) {
                    for ((index, entry) in values.entries.withIndex()) {
                        bindValue(index = index, column = entry.key, value = entry.value)
                    }
                }
            }
        )
        emit(Unit)
    }

    public override fun delete(table: String, ids: List<Long>): Flow<Unit> = flow {
        if (ids.isNotEmpty()) {
            val sql = buildDeleteQuery(table, ids)
            driver.execute(identifier = null, sql = sql, parameters = 0)
        }
        emit(Unit)
    }

    override fun raw(sql: String): Flow<Unit> = flow {
        driver.execute(identifier = null, sql = sql, parameters = 0)
        emit(Unit)
    }
}

@OptIn(ExperimentalStdlibApi::class)
private fun SqlPreparedStatement.bindValue(index: Int, column: Column, value: String?) {
    when (column.type) {
        ColumnType.INTEGER -> bindLong(index, value?.toLong())
        ColumnType.TEXT -> bindString(index, value)
        ColumnType.REAL -> bindDouble(index, value?.toDouble())
        ColumnType.BLOB -> bindBytes(index, value?.hexToByteArray())
    }
}
