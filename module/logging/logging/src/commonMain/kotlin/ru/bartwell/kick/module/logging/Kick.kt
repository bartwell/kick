package ru.bartwell.kick.module.logging

import ru.bartwell.kick.Kick
import ru.bartwell.kick.module.logging.core.data.LogLevel
import ru.bartwell.kick.module.logging.core.util.Logger

public fun Kick.Companion.log(level: LogLevel, message: String?) {
    Logger.log(level, message)
}
