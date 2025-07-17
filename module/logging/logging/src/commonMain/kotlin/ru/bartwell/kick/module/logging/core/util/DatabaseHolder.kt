package ru.bartwell.kick.module.logging.core.util

import ru.bartwell.kick.module.logging.core.persist.LoggingDatabase

internal object DatabaseHolder {
    var database: LoggingDatabase? = null
}
