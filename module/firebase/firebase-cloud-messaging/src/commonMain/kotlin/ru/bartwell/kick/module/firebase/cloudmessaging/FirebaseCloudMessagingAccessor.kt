package ru.bartwell.kick.module.firebase.cloudmessaging

import ru.bartwell.kick.module.firebase.cloudmessaging.core.actions.FirebaseCloudMessagingActions
import ru.bartwell.kick.module.firebase.cloudmessaging.core.data.FirebaseMessage

public class FirebaseCloudMessagingAccessor internal constructor() {
    public fun log(message: FirebaseMessage) {
        FirebaseCloudMessagingActions.emitMessage(message)
    }
}
