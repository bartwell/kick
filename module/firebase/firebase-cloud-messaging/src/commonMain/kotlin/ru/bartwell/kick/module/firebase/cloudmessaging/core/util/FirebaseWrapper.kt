package ru.bartwell.kick.module.firebase.cloudmessaging.core.util

import ru.bartwell.kick.core.data.PlatformContext

internal expect object FirebaseWrapper {
    val supportsAutoFetch: Boolean

    fun isFirebaseInitialized(context: PlatformContext): Boolean

    suspend fun getRegistrationToken(
        context: PlatformContext,
        forceRefresh: Boolean,
    ): Result<String>

    suspend fun getFirebaseInstallationId(
        context: PlatformContext,
    ): Result<String>
}
