package ru.bartwell.kick.module.sqlite.adapter.sqldelight

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import ru.bartwell.kick.module.sqlite.core.DatabaseWrapper
import ru.bartwell.kick.module.sqlite.core.data.Column
import ru.bartwell.kick.module.sqlite.core.mapper.SqlMapper

@Suppress("UnusedPrivateProperty", "EmptyFunctionBlock", "unused")
public class SqlDelightWrapper(private val driver: Any) : DatabaseWrapper() {

    override val type: Type = Type.SQL_DELIGHT

    override fun <T> query(sql: String, mapper: SqlMapper<T>): Flow<List<T>> = flowOf(emptyList())

    override fun <T> querySingle(sql: String, mapper: SqlMapper<T>): Flow<T?> = flowOf(null)

    override fun updateSingle(table: String, id: Long, column: Column, value: String?): Flow<Unit> = flowOf(Unit)

    override fun insert(table: String, values: Map<Column, String?>): Flow<Unit> = flowOf(Unit)

    override fun delete(table: String, ids: List<Long>): Flow<Unit> = flowOf(Unit)

    override fun raw(sql: String): Flow<Unit> = flowOf(Unit)
}
