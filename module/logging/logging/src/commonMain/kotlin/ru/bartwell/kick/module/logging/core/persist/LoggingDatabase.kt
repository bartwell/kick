package ru.bartwell.kick.module.logging.core.persist

import ru.bartwell.kick.module.logging.db.LoggingDb

internal class LoggingDatabase(private val db: LoggingDb) {
    fun getLogDao(): LogDao = LogDao(db)
}
