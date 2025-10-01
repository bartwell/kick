package ru.bartwell.kick.module.logging.core.persist

import ru.bartwell.kick.module.logging.core.data.LogLevel

public data class LogEntity(
    val id: Long = 0,
    val time: Long,
    val level: LogLevel,
    val message: String,
)
