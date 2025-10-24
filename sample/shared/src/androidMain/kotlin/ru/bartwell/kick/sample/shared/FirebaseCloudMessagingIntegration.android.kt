package ru.bartwell.kick.sample.shared

import ru.bartwell.kick.Kick
import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.core.data.get
import ru.bartwell.kick.module.firebase.cloudmessaging.FirebaseCloudMessagingModule
import ru.bartwell.kick.module.firebase.cloudmessaging.core.actions.AndroidFirebaseCloudMessagingDelegate
import ru.bartwell.kick.module.firebase.cloudmessaging.firebaseCloudMessaging

actual fun Kick.Configuration.registerFirebaseCloudMessagingModule(context: PlatformContext) {
    module(FirebaseCloudMessagingModule(context))
}

actual fun setupFirebaseCloudMessagingIntegration(context: PlatformContext) {
    val appContext = context.get().applicationContext
    val delegate = firebaseCloudMessagingDelegate
        ?: AndroidFirebaseCloudMessagingDelegate(
            appContext,
            DEFAULT_CHANNEL_ID,
            DEFAULT_CHANNEL_NAME,
        ).also { firebaseCloudMessagingDelegate = it }
    Kick.firebaseCloudMessaging.registerDelegate(delegate)
}

private var firebaseCloudMessagingDelegate: AndroidFirebaseCloudMessagingDelegate? = null

private const val DEFAULT_CHANNEL_ID: String = "kick_firebase_debug"
private const val DEFAULT_CHANNEL_NAME: String = "Firebase Debug Pushes"
