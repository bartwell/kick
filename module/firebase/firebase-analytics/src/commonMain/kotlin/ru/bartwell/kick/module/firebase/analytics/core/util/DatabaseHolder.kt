package ru.bartwell.kick.module.firebase.analytics.core.util

import ru.bartwell.kick.module.firebase.analytics.core.persist.FirebaseAnalyticsDatabase

internal object DatabaseHolder {
    var database: FirebaseAnalyticsDatabase? = null
}
