package ru.bartwell.kick.module.firebase.cloudmessaging

import ru.bartwell.kick.module.firebase.cloudmessaging.core.actions.FirebaseCloudMessagingDelegate
import ru.bartwell.kick.module.firebase.cloudmessaging.core.data.FirebaseMessage

public class FirebaseCloudMessagingAccessor internal constructor() {
    public fun registerDelegate(delegate: FirebaseCloudMessagingDelegate) {}
    public fun unregisterDelegate(delegate: FirebaseCloudMessagingDelegate? = null) {}
    public fun log(message: FirebaseMessage) {}
    public fun clearLoggedMessages() {}
}
