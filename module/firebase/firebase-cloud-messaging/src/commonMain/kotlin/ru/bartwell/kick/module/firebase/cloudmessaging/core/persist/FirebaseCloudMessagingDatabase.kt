package ru.bartwell.kick.module.firebase.cloudmessaging.core.persist

import ru.bartwell.kick.module.firebase.cloudmessaging.db.FirebaseCloudMessagingDb

internal class FirebaseCloudMessagingDatabase(private val db: FirebaseCloudMessagingDb) {
    fun getMessageDao(): FirebaseMessageDao = FirebaseMessageDao(db)
}
