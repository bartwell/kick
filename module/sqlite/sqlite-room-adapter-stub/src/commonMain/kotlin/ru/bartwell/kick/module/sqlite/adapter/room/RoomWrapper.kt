package ru.bartwell.kick.module.sqlite.adapter.room

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import ru.bartwell.kick.core.DatabaseWrapper
import ru.bartwell.kick.core.data.Column
import ru.bartwell.kick.core.mapper.SqlMapper

@Suppress("UnusedPrivateProperty", "EmptyFunctionBlock", "unused")
public class RoomWrapper(internal val database: Any) : DatabaseWrapper() {

    override val type: Type = Type.ROOM

    override fun <T> query(sql: String, mapper: SqlMapper<T>): Flow<List<T>> = flowOf(emptyList())

    override fun <T> querySingle(sql: String, mapper: SqlMapper<T>): Flow<T?> = flowOf(null)

    override fun updateSingle(table: String, id: Long, column: Column, value: String?): Flow<Unit> = flowOf(Unit)

    override fun insert(table: String, values: Map<Column, String?>): Flow<Unit> = flowOf(Unit)

    override fun delete(table: String, ids: List<Long>): Flow<Unit> = flowOf(Unit)

    override fun raw(sql: String): Flow<Unit> = flowOf(Unit)
}
