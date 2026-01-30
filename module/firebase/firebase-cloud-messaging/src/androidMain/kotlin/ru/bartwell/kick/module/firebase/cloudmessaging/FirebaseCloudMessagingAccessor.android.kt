package ru.bartwell.kick.module.firebase.cloudmessaging

import com.google.firebase.messaging.RemoteMessage
import ru.bartwell.kick.module.firebase.cloudmessaging.core.data.toFirebaseMessage
import ru.bartwell.kick.module.firebase.cloudmessaging.core.util.FirebaseMessageLogger

public fun FirebaseCloudMessagingAccessor.handleFcm(message: RemoteMessage) {
    FirebaseMessageLogger.log(message.toFirebaseMessage())
}
