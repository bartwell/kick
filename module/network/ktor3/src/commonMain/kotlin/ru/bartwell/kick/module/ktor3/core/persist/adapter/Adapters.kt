package ru.bartwell.kick.module.ktor3.core.persist.adapter

import app.cash.sqldelight.ColumnAdapter
import ru.bartwell.kick.module.ktor3.feature.list.data.HttpMethod

internal val httpMethodAdapter: ColumnAdapter<HttpMethod, String> = object : ColumnAdapter<HttpMethod, String> {
    override fun decode(databaseValue: String): HttpMethod = HttpMethod.fromString(databaseValue)
    override fun encode(value: HttpMethod): String = value.name
}

internal val booleanAdapter: ColumnAdapter<Boolean, Long> = object : ColumnAdapter<Boolean, Long> {
    override fun decode(databaseValue: Long): Boolean = databaseValue != 0L
    override fun encode(value: Boolean): Long = if (value) 1L else 0L
}
