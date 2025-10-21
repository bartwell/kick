package ru.bartwell.kick.module.firebase.cloudmessaging.core.data

import kotlin.system.getTimeMillis

/**
 * Representation of a Firebase Cloud Messaging push notification that can be shown inside Kick.
 */
public data class FirebaseMessage(
    val title: String? = null,
    val body: String? = null,
    val data: Map<String, String> = emptyMap(),
    val from: String? = null,
    val to: String? = null,
    val messageId: String? = null,
    val sentTimeMillis: Long? = null,
    val collapseKey: String? = null,
    val channelId: String? = null,
    val category: String? = null,
    val threadId: String? = null,
    val badge: String? = null,
    val sound: String? = null,
    val tag: String? = null,
    val imageUrl: String? = null,
    val priority: String? = null,
    val ttlSeconds: Long? = null,
    val raw: Map<String, String> = emptyMap(),
    val receivedAtMillis: Long = getTimeMillis(),
)
