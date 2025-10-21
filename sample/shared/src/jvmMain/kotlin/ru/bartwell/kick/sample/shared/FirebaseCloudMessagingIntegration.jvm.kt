package ru.bartwell.kick.sample.shared

import ru.bartwell.kick.Kick
import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.module.firebase.cloudmessaging.FirebaseCloudMessagingModule

actual fun Kick.Configuration.registerFirebaseCloudMessagingModule(context: PlatformContext) {
    module(FirebaseCloudMessagingModule(context))
}

actual fun setupFirebaseCloudMessagingIntegration(context: PlatformContext) {
    // Firebase Cloud Messaging is not available on the desktop sample target.
}
