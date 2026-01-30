package ru.bartwell.kick.module.firebase.cloudmessaging.core.util

import ru.bartwell.kick.module.firebase.cloudmessaging.core.persist.FirebaseCloudMessagingDatabase

internal object DatabaseHolder {
    var database: FirebaseCloudMessagingDatabase? = null
}
