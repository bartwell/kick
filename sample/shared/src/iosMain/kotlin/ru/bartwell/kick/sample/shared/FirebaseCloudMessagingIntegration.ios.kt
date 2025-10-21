package ru.bartwell.kick.sample.shared

import ru.bartwell.kick.Kick
import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.module.firebase.cloudmessaging.FirebaseCloudMessagingModule

actual fun Kick.Configuration.registerFirebaseCloudMessagingModule(context: PlatformContext) {
    module(FirebaseCloudMessagingModule(context))
}

actual fun setupFirebaseCloudMessagingIntegration(context: PlatformContext) {
    // The sample application does not bundle Firebase on iOS.
    // Host apps should register their own FirebaseCloudMessagingDelegate implementation.
}
