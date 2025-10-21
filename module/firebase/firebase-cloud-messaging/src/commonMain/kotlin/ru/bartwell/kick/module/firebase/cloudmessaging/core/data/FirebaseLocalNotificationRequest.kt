package ru.bartwell.kick.module.firebase.cloudmessaging.core.data

/**
 * Parameters required to trigger a local notification that emulates an incoming FCM push.
 */
public data class FirebaseLocalNotificationRequest(
    val title: String?,
    val body: String?,
    val data: Map<String, String>,
    val channelId: String?,
)
