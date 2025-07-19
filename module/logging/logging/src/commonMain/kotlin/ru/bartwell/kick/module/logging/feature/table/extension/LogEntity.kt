package ru.bartwell.kick.module.logging.feature.table.extension

import ru.bartwell.kick.core.util.DateUtils
import ru.bartwell.kick.module.logging.core.persist.LogEntity

internal fun LogEntity.toLogString() = buildString {
    append(level.name.first())
    append("/ ")
    append(DateUtils.formatLogTime(time))
    append(" ")
    append(message)
}
