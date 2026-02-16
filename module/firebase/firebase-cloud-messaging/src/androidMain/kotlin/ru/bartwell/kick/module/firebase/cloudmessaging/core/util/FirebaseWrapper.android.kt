package ru.bartwell.kick.module.firebase.cloudmessaging.core.util

import android.content.Context
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.suspendCancellableCoroutine
import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.core.data.get
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

internal actual object FirebaseWrapper {
    actual val supportsAutoFetch: Boolean = true

    actual fun isFirebaseInitialized(
        context: PlatformContext,
    ): Boolean {
        val appContext = context.get().applicationContext
        return FirebaseApp.getApps(appContext).isNotEmpty()
    }

    actual suspend fun getRegistrationToken(
        context: PlatformContext,
        forceRefresh: Boolean,
    ): Result<String> = runCatching {
        val appContext = context.get().applicationContext
        if (!ensureFirebaseInitialized(appContext)) {
            throw FirebaseNotInitializedException()
        }
        val messaging = FirebaseMessaging.getInstance()
        if (forceRefresh) {
            messaging.deleteToken().await()
        }
        messaging.token.await()
    }

    actual suspend fun getFirebaseInstallationId(
        context: PlatformContext,
    ): Result<String> = runCatching {
        val appContext = context.get().applicationContext
        if (!ensureFirebaseInitialized(appContext)) {
            throw FirebaseNotInitializedException()
        }
        FirebaseInstallations.getInstance().id.await()
    }
}

private fun ensureFirebaseInitialized(appContext: Context): Boolean {
    return FirebaseApp.getApps(appContext).isNotEmpty()
}

private class FirebaseNotInitializedException : IllegalStateException(
    "Firebase is not initialised",
)

private suspend fun <T> Task<T>.await(): T = suspendCancellableCoroutine { continuation ->
    addOnCompleteListener { task ->
        if (task.isSuccessful) {
            continuation.resume(task.result)
        } else {
            val exception = task.exception ?: IllegalStateException("Firebase task failed")
            continuation.resumeWithException(exception)
        }
    }
}
