package ru.bartwell.kick.module.sqlite.adapter.room

import androidx.room.RoomDatabase
import androidx.room.execSQL
import androidx.room.useReaderConnection
import androidx.room.useWriterConnection
import androidx.sqlite.SQLiteStatement
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.bartwell.kick.module.sqlite.core.DatabaseWrapper
import ru.bartwell.kick.module.sqlite.core.data.Column
import ru.bartwell.kick.module.sqlite.core.data.ColumnType
import ru.bartwell.kick.module.sqlite.core.mapper.SqlMapper

public class RoomWrapper(internal val database: RoomDatabase) : DatabaseWrapper() {

    override val type: Type = Type.ROOM

    override fun <T> query(sql: String, mapper: SqlMapper<T>): Flow<List<T>> = flow {
        val result = mutableListOf<T>()
        database.useReaderConnection { connection ->
            connection.usePrepared(sql) { statement ->
                while (statement.step()) {
                    mapper.map(RoomCursorWrapper(statement))?.let {
                        result.add(it)
                    }
                }
            }
        }
        emit(result)
    }

    override fun <T> querySingle(sql: String, mapper: SqlMapper<T>): Flow<T?> = flow {
        var result: T? = null
        database.useReaderConnection { connection ->
            connection.usePrepared(sql) { statement ->
                statement.step()
                result = mapper.map(RoomCursorWrapper(statement))
            }
        }
        emit(result)
    }

    override fun updateSingle(table: String, id: Long, column: Column, value: String?): Flow<Unit> = flow {
        val sql = buildUpdateQuery(table, column)
        database.useWriterConnection { connection ->
            connection.usePrepared(sql) { statement ->
                statement.bindValue(index = 1, column = column, value = value)
                statement.bindLong(index = 2, value = id)
                statement.step()
            }
        }
        emit(Unit)
    }

    override fun insert(table: String, values: Map<Column, String?>): Flow<Unit> = flow {
        val sql = buildInsertQuery(table, values)
        database.useWriterConnection { connection ->
            connection.usePrepared(sql) { statement ->
                if (values.isNotEmpty()) {
                    for ((index, entry) in values.entries.withIndex()) {
                        statement.bindValue(index = index + 1, column = entry.key, value = entry.value)
                    }
                }
                statement.step()
            }
        }
        emit(Unit)
    }

    override fun delete(table: String, ids: List<Long>): Flow<Unit> = flow {
        if (ids.isNotEmpty()) {
            val sql = buildDeleteQuery(table, ids)
            database.useWriterConnection { connection ->
                connection.usePrepared(sql) { statement ->
                    statement.step()
                }
            }
        }
        emit(Unit)
    }

    override fun raw(sql: String): Flow<Unit> = flow {
        database.useWriterConnection { connection ->
            connection.execSQL(sql)
        }
        emit(Unit)
    }
}

private fun SQLiteStatement.bindLong(index: Int, value: Long?) = value?.let {
    bindLong(index, it)
} ?: bindNull(index)

private fun SQLiteStatement.bindText(index: Int, value: String?) = value?.let {
    bindText(index, it)
} ?: bindNull(index)

private fun SQLiteStatement.bindDouble(index: Int, value: Double?) = value?.let {
    bindDouble(index, it)
} ?: bindNull(index)

private fun SQLiteStatement.bindBlob(index: Int, value: ByteArray?) = value?.let {
    bindBlob(index, it)
} ?: bindNull(index)

@OptIn(ExperimentalStdlibApi::class)
private fun SQLiteStatement.bindValue(index: Int, column: Column, value: String?) {
    when (column.type) {
        ColumnType.INTEGER -> this.bindLong(index, value?.toLong())
        ColumnType.TEXT -> this.bindText(index, value)
        ColumnType.REAL -> this.bindDouble(index, value?.toDouble())
        ColumnType.BLOB -> this.bindBlob(index, value?.hexToByteArray())
    }
}
