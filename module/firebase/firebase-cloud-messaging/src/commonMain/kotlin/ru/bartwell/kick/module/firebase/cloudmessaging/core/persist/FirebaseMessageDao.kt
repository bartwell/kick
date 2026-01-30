package ru.bartwell.kick.module.firebase.cloudmessaging.core.persist

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import ru.bartwell.kick.module.firebase.cloudmessaging.db.FcmMessage
import ru.bartwell.kick.module.firebase.cloudmessaging.db.FirebaseCloudMessagingDb

internal class FirebaseMessageDao(private val db: FirebaseCloudMessagingDb) {

    suspend fun insert(item: FirebaseMessageEntity) = withContext(Dispatchers.Default) {
        db.fcm_messageQueries.insertMessage(
            title = item.title,
            body = item.body,
            dataPayload = item.data,
            fromAddress = item.from,
            toAddress = item.to,
            messageId = item.messageId,
            sentTimeMillis = item.sentTimeMillis,
            collapseKey = item.collapseKey,
            channelId = item.channelId,
            category = item.category,
            threadId = item.threadId,
            badge = item.badge,
            sound = item.sound,
            tag = item.tag,
            imageUrl = item.imageUrl,
            priority = item.priority,
            ttlSeconds = item.ttlSeconds,
            rawPayload = item.raw,
            receivedAtMillis = item.receivedAtMillis,
        )
    }

    fun getAllAsFlow(): Flow<List<FirebaseMessageEntity>> =
        db.fcm_messageQueries
            .selectAll()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { list -> list.map { it.toEntity() } }

    suspend fun deleteAll() = withContext(Dispatchers.Default) {
        db.fcm_messageQueries.deleteAll()
    }

    suspend fun trimToSize(maxItems: Long) = withContext(Dispatchers.Default) {
        db.fcm_messageQueries.trimToSize(maxItems)
    }
}

private fun FcmMessage.toEntity(): FirebaseMessageEntity = FirebaseMessageEntity(
    id = id,
    title = title,
    body = body,
    data = dataPayload,
    from = fromAddress,
    to = toAddress,
    messageId = messageId,
    sentTimeMillis = sentTimeMillis,
    collapseKey = collapseKey,
    channelId = channelId,
    category = category,
    threadId = threadId,
    badge = badge,
    sound = sound,
    tag = tag,
    imageUrl = imageUrl,
    priority = priority,
    ttlSeconds = ttlSeconds,
    raw = rawPayload,
    receivedAtMillis = receivedAtMillis,
)
