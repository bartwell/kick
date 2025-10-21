package ru.bartwell.kick.module.firebase.cloudmessaging

import ru.bartwell.kick.module.firebase.cloudmessaging.core.actions.FirebaseCloudMessagingActions
import ru.bartwell.kick.module.firebase.cloudmessaging.core.actions.FirebaseCloudMessagingDelegate
import ru.bartwell.kick.module.firebase.cloudmessaging.core.data.FirebaseMessage

public class FirebaseCloudMessagingAccessor internal constructor() {
    public fun registerDelegate(delegate: FirebaseCloudMessagingDelegate) {
        FirebaseCloudMessagingActions.registerDelegate(delegate)
    }

    public fun unregisterDelegate(delegate: FirebaseCloudMessagingDelegate? = null) {
        FirebaseCloudMessagingActions.unregisterDelegate(delegate ?: FirebaseCloudMessagingActions.currentDelegate())
    }

    public fun log(message: FirebaseMessage) {
        FirebaseCloudMessagingActions.emitMessage(message)
    }

    public fun clearLoggedMessages() {
        FirebaseCloudMessagingActions.clearMessages()
    }
}
