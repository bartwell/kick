package ru.bartwell.kick.module.logging

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import ru.bartwell.kick.Kick
import ru.bartwell.kick.core.util.DateUtils
import ru.bartwell.kick.module.logging.core.data.LogLevel
import ru.bartwell.kick.module.logging.core.persist.LogEntity
import ru.bartwell.kick.module.logging.core.util.DatabaseHolder

public fun Kick.Companion.log(level: LogLevel, message: String?) {
    message?.let {
        CoroutineScope(Dispatchers.IO).launch {
            DatabaseHolder.database
                ?.getLogDao()
                ?.insert(
                    LogEntity(
                        time = DateUtils.currentTimeMillis(),
                        level = level,
                        message = message,
                    )
                )
        }
    }
}
