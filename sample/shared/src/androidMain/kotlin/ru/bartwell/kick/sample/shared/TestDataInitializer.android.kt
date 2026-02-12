package ru.bartwell.kick.sample.shared

import ru.bartwell.kick.core.data.Module
import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.module.firebase.analytics.FirebaseAnalyticsModule
import ru.bartwell.kick.module.firebase.cloudmessaging.FirebaseCloudMessagingModule

actual fun createFirebaseCloudMessagingModule(context: PlatformContext): Module? =
    FirebaseCloudMessagingModule(context)

actual fun createFirebaseAnalyticsModule(context: PlatformContext): Module? =
    FirebaseAnalyticsModule(context)
