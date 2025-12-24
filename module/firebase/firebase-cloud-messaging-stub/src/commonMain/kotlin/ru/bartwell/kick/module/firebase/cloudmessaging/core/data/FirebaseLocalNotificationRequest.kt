package ru.bartwell.kick.module.firebase.cloudmessaging.core.data

public data class FirebaseLocalNotificationRequest(
    val title: String?,
    val body: String?,
    val data: Map<String, String>,
    val channelId: String?,
    val channelName: String? = null,
    val channelImportance: FirebaseNotificationImportance? = null,
    val smallIconResId: Int? = null,
    val pendingIntent: PlatformPendingIntent? = null,
)
