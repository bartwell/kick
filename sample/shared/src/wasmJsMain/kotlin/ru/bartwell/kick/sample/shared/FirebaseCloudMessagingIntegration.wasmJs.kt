package ru.bartwell.kick.sample.shared

import ru.bartwell.kick.Kick
import ru.bartwell.kick.core.data.PlatformContext

actual fun Kick.Configuration.registerFirebaseCloudMessagingModule(context: PlatformContext) {
    // Firebase Cloud Messaging UI is not exposed for the web sample target.
}

actual fun setupFirebaseCloudMessagingIntegration(context: PlatformContext) {
    // No Firebase support on the web sample target.
}
