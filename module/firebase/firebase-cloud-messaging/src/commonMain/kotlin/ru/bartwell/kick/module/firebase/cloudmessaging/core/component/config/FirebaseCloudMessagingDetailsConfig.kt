package ru.bartwell.kick.module.firebase.cloudmessaging.core.component.config

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.bartwell.kick.core.component.Config
import ru.bartwell.kick.module.firebase.cloudmessaging.core.data.FirebaseMessage

@Serializable
@SerialName("FirebaseCloudMessagingDetails")
internal data class FirebaseCloudMessagingDetailsConfig(
    val message: FirebaseMessage,
) : Config
