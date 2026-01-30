package ru.bartwell.kick.module.firebase.cloudmessaging.feature.history.presentation

import ru.bartwell.kick.module.firebase.cloudmessaging.core.data.FirebaseMessage

internal data class FirebaseCloudMessagingHistoryState(
    val messages: List<FirebaseMessage> = emptyList(),
)
