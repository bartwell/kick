package ru.bartwell.kick.sample.shared

import android.content.Context
import ru.bartwell.kick.Kick
import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.core.data.get
import ru.bartwell.kick.module.firebase.cloudmessaging.FirebaseCloudMessagingModule
import ru.bartwell.kick.module.firebase.cloudmessaging.core.actions.FirebaseCloudMessagingDelegate
import ru.bartwell.kick.module.firebase.cloudmessaging.firebaseCloudMessaging

actual fun Kick.Configuration.registerFirebaseCloudMessagingModule(context: PlatformContext) {
    module(FirebaseCloudMessagingModule(context))
}

actual fun setupFirebaseCloudMessagingIntegration(context: PlatformContext) {
    val delegate = firebaseCloudMessagingDelegate
        ?: createFirebaseDelegate(context.get().applicationContext).also { firebaseCloudMessagingDelegate = it }
    delegate?.let { Kick.firebaseCloudMessaging.registerDelegate(it) }
}

private var firebaseCloudMessagingDelegate: FirebaseCloudMessagingDelegate? = null

private fun createFirebaseDelegate(context: Context): FirebaseCloudMessagingDelegate? {
    return runCatching {
        val clazz = Class.forName(
            "ru.bartwell.kick.module.firebase.cloudmessaging.core.actions.AndroidFirebaseCloudMessagingDelegate"
        )
        val constructor = clazz.getConstructor(Context::class.java, String::class.java, String::class.java)
        @Suppress("UNCHECKED_CAST")
        constructor.newInstance(context, DEFAULT_CHANNEL_ID, DEFAULT_CHANNEL_NAME) as FirebaseCloudMessagingDelegate
    }.getOrNull()
}

private const val DEFAULT_CHANNEL_ID: String = "kick_firebase_debug"
private const val DEFAULT_CHANNEL_NAME: String = "Firebase Debug Pushes"
