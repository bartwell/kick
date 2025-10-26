package ru.bartwell.kick.sample.android

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import ru.bartwell.kick.Kick
import ru.bartwell.kick.module.firebase.cloudmessaging.firebaseCloudMessaging
import ru.bartwell.kick.module.firebase.cloudmessaging.handleFcm

class KickFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        Kick.firebaseCloudMessaging.handleFcm(message)
    }
}
