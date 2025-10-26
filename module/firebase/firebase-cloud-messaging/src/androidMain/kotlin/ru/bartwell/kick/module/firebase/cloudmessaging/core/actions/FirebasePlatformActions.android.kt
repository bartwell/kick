package ru.bartwell.kick.module.firebase.cloudmessaging.core.actions

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessaging
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine
import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.core.data.get
import ru.bartwell.kick.module.firebase.cloudmessaging.core.data.AndroidNotificationChannelStatus
import ru.bartwell.kick.module.firebase.cloudmessaging.core.data.FirebaseLocalNotificationRequest
import ru.bartwell.kick.module.firebase.cloudmessaging.core.data.FirebaseNotificationStatus

private var applicationContext: Context? = null

internal actual fun platformInitialize(context: PlatformContext) {
    applicationContext = context.get().applicationContext
}

internal actual fun platformIsFirebaseInitialized(): Boolean {
    val context = applicationContext ?: return false
    return FirebaseApp.getApps(context).isNotEmpty()
}

internal actual suspend fun platformGetRegistrationToken(
    forceRefresh: Boolean,
): Result<String> = runCatching {
    val context = requireContext()
    ensureFirebaseInitialized(context)
    val messaging = FirebaseMessaging.getInstance()
    if (forceRefresh) {
        messaging.deleteToken().await()
    }
    messaging.token.await()
}

internal actual suspend fun platformGetFirebaseInstallationId(): Result<String> = runCatching {
    val context = requireContext()
    ensureFirebaseInitialized(context)
    FirebaseInstallations.getInstance().id.await()
}

internal actual suspend fun platformSendLocalNotification(
    request: FirebaseLocalNotificationRequest,
): Result<Unit> = runCatching {
    val context = requireContext()
    val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val channelId = ensureChannel(manager, request.channelId)
    val contentText = request.body ?: request.data.entries.joinToString { "${'$'}{it.key}: ${'$'}{it.value}" }
    val notification = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(android.R.drawable.ic_dialog_info)
        .setContentTitle(request.title ?: "Firebase push")
        .setContentText(contentText)
        .setStyle(NotificationCompat.BigTextStyle().bigText(contentText))
        .setAutoCancel(true)
        .build()
    try {
        manager.notify(System.currentTimeMillis().toInt(), notification)
    } catch (error: SecurityException) {
        throw LocalNotificationException(
            status = collectNotificationStatus(context, manager),
            cause = error,
        )
    }
}

internal actual suspend fun platformGetNotificationStatus(): Result<FirebaseNotificationStatus> = runCatching {
    val context = requireContext()
    ensureFirebaseInitialized(context)
    val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    collectNotificationStatus(context, manager)
}

private fun collectNotificationStatus(
    context: Context,
    manager: NotificationManager,
): FirebaseNotificationStatus {
    val channelInfo = buildChannelStatus(manager)
    val playServices = GoogleApiAvailability.getInstance()
        .isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS
    return FirebaseNotificationStatus(
        androidChannel = channelInfo,
        isGooglePlayServicesAvailable = playServices,
    )
}

private fun buildChannelStatus(manager: NotificationManager): AndroidNotificationChannelStatus {
    val notificationsEnabled = manager.areNotificationsEnabled()
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = manager.getNotificationChannel(DEFAULT_CHANNEL_ID)
        AndroidNotificationChannelStatus(
            id = channel?.id ?: DEFAULT_CHANNEL_ID,
            name = channel?.name?.toString(),
            description = channel?.description,
            importance = channel?.importance?.toImportanceDescription(),
            isEnabled = channel != null && notificationsEnabled,
            isAppNotificationsEnabled = notificationsEnabled,
        )
    } else {
        AndroidNotificationChannelStatus(
            id = DEFAULT_CHANNEL_ID,
            name = null,
            description = null,
            importance = null,
            isEnabled = notificationsEnabled,
            isAppNotificationsEnabled = notificationsEnabled,
        )
    }
}

private fun ensureChannel(manager: NotificationManager, requested: String?): String {
    val id = requested ?: DEFAULT_CHANNEL_ID
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val existing = manager.getNotificationChannel(id)
        if (existing == null) {
            val channel = NotificationChannel(id, DEFAULT_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            manager.createNotificationChannel(channel)
        }
    }
    return id
}

private fun ensureFirebaseInitialized(context: Context) {
    require(FirebaseApp.getApps(context).isNotEmpty()) { "Firebase is not initialised" }
}

private fun requireContext(): Context = applicationContext
    ?: throw IllegalStateException("Firebase Cloud Messaging module is not initialised with Android context")

private class LocalNotificationException(
    val status: FirebaseNotificationStatus,
    cause: Throwable,
) : IllegalStateException(
    buildString {
        append("Failed to display local notification. ")
        append(cause.message ?: cause::class.java.simpleName)
        append(". Ensure notification permissions are granted.")
        status.androidChannel?.let { channel ->
            append(" Channel '")
            append(channel.id)
            append("' is")
            append(if (channel.isEnabled == true) " enabled" else " disabled")
            channel.isAppNotificationsEnabled?.let { enabled ->
                append(", app notifications are ")
                append(if (enabled) "allowed" else "blocked")
            }
        }
        status.isGooglePlayServicesAvailable?.let { available ->
            append(". Google Play Services: ")
            append(if (available) "available" else "unavailable")
        }
    },
    cause,
)

private fun Int.toImportanceDescription(): String = when (this) {
    NotificationManager.IMPORTANCE_NONE -> "none"
    NotificationManager.IMPORTANCE_MIN -> "min"
    NotificationManager.IMPORTANCE_LOW -> "low"
    NotificationManager.IMPORTANCE_DEFAULT -> "default"
    NotificationManager.IMPORTANCE_HIGH -> "high"
    NotificationManager.IMPORTANCE_MAX -> "max"
    else -> toString()
}

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

private const val DEFAULT_CHANNEL_ID: String = "kick_firebase_debug"
private const val DEFAULT_CHANNEL_NAME: String = "Firebase Debug Pushes"
