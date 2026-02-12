package ru.bartwell.kick.module.firebase.analytics

import ru.bartwell.kick.Kick

public val Kick.Companion.firebaseAnalytics: FirebaseAnalyticsAccessor
    get() = FirebaseAnalyticsAccessor()
