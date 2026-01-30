package ru.bartwell.kick.module.firebase.analytics.core.persist

import ru.bartwell.kick.module.firebase.analytics.db.FirebaseAnalyticsDb

internal class FirebaseAnalyticsDatabase(private val db: FirebaseAnalyticsDb) {
    fun getEventDao(): AnalyticsEventDao = AnalyticsEventDao(db)
    fun getPropertyDao(): UserPropertyDao = UserPropertyDao(db)
    fun getUserIdDao(): UserIdDao = UserIdDao(db)
}
