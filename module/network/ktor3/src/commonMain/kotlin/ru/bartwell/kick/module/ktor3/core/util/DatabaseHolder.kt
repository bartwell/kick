package ru.bartwell.kick.module.ktor3.core.util

import ru.bartwell.kick.module.ktor3.core.persist.Ktor3Database

internal object DatabaseHolder {
    var database: Ktor3Database? = null
}
