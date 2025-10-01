package ru.bartwell.kick.module.logging.core.persist.adapter

import app.cash.sqldelight.ColumnAdapter
import ru.bartwell.kick.module.logging.core.data.LogLevel

internal val logLevelAdapter: ColumnAdapter<LogLevel, String> = object : ColumnAdapter<LogLevel, String> {
    override fun decode(databaseValue: String): LogLevel = LogLevel.valueOf(databaseValue)
    override fun encode(value: LogLevel): String = value.name
}
