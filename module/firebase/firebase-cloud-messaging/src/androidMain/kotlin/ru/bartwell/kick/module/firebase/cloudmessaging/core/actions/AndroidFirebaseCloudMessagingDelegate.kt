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
import ru.bartwell.kick.module.firebase.cloudmessaging.core.data.AndroidNotificationChannelStatus
import ru.bartwell.kick.module.firebase.cloudmessaging.core.data.FirebaseLocalNotificationRequest
import ru.bartwell.kick.module.firebase.cloudmessaging.core.data.FirebaseNotificationStatus

public class AndroidFirebaseCloudMessagingDelegate(
    private val context: Context,
    private val fallbackChannelId: String = DEFAULT_CHANNEL_ID,
    private val fallbackChannelName: String = DEFAULT_CHANNEL_NAME,
) : FirebaseCloudMessagingDelegate {

    private val applicationContext: Context = context.applicationContext

    override val isFirebaseInitialized: Boolean
        get() = FirebaseApp.getApps(applicationContext).isNotEmpty()

    override suspend fun getRegistrationToken(forceRefresh: Boolean): Result<String> = runCatching {
        ensureFirebase()
        val messaging = FirebaseMessaging.getInstance()
        if (forceRefresh) {
            messaging.deleteToken().await()
        }
        messaging.token.await()
    }

    override suspend fun getFirebaseInstallationId(): Result<String> = runCatching {
        ensureFirebase()
        FirebaseInstallations.getInstance().id.await()
    }

    override suspend fun sendLocalNotification(request: FirebaseLocalNotificationRequest): Result<Unit> = runCatching {
        ensureFirebase()
        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = ensureChannel(manager, request.channelId)
        val contentText = request.body ?: request.data.entries.joinToString { "${it.key}: ${it.value}" }
        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(request.title ?: "Firebase push")
            .setContentText(contentText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(contentText))
            .setAutoCancel(true)
            .build()
        manager.notify(System.currentTimeMillis().toInt(), notification)
    }

    override suspend fun getNotificationStatus(): Result<FirebaseNotificationStatus> = runCatching {
        ensureFirebase()
        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelInfo = buildChannelStatus(manager)
        val playServices = GoogleApiAvailability.getInstance()
            .isGooglePlayServicesAvailable(applicationContext) == ConnectionResult.SUCCESS
        FirebaseNotificationStatus(
            androidChannel = channelInfo,
            isGooglePlayServicesAvailable = playServices,
        )
    }

    private fun buildChannelStatus(manager: NotificationManager): AndroidNotificationChannelStatus {
        val notificationsEnabled = manager.areNotificationsEnabled()
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = manager.getNotificationChannel(fallbackChannelId)
            AndroidNotificationChannelStatus(
                id = channel?.id ?: fallbackChannelId,
                name = channel?.name?.toString(),
                description = channel?.description,
                importance = channel?.importance?.toImportanceDescription(),
                isEnabled = channel != null && notificationsEnabled,
                isAppNotificationsEnabled = notificationsEnabled,
            )
        } else {
            AndroidNotificationChannelStatus(
                id = fallbackChannelId,
                name = null,
                description = null,
                importance = null,
                isEnabled = notificationsEnabled,
                isAppNotificationsEnabled = notificationsEnabled,
            )
        }
    }

    private fun ensureChannel(manager: NotificationManager, requested: String?): String {
        val id = requested ?: fallbackChannelId
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val existing = manager.getNotificationChannel(id)
            if (existing == null) {
                val channel = NotificationChannel(id, fallbackChannelName, NotificationManager.IMPORTANCE_DEFAULT)
                manager.createNotificationChannel(channel)
            }
        }
        return id
    }

    private fun ensureFirebase() {
        require(isFirebaseInitialized) { "Firebase is not initialised" }
    }

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

    private companion object {
        private const val DEFAULT_CHANNEL_ID: String = "kick_firebase_debug"
        private const val DEFAULT_CHANNEL_NAME: String = "Firebase Debug Pushes"
    }
}
