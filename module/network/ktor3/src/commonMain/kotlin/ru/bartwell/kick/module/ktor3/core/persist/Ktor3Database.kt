package ru.bartwell.kick.module.ktor3.core.persist

import ru.bartwell.kick.module.ktor3.db.Ktor3Db

internal class Ktor3Database(private val db: Ktor3Db) {
    fun getRequestDao(): RequestDao = RequestDao(db)
}
