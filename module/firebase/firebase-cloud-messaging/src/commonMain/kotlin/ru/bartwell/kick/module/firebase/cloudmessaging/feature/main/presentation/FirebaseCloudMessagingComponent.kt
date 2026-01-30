package ru.bartwell.kick.module.firebase.cloudmessaging.feature.main.presentation

import com.arkivanov.decompose.value.Value
import ru.bartwell.kick.core.component.Component
import ru.bartwell.kick.core.data.PlatformContext

internal interface FirebaseCloudMessagingComponent : Component {
    val state: Value<FirebaseCloudMessagingState>
    fun init(context: PlatformContext)
    fun onBackPressed()
    fun refreshToken(context: PlatformContext, forceRefresh: Boolean)
    fun refreshFirebaseId(context: PlatformContext)
    fun copyToken(context: PlatformContext)
    fun copyFirebaseId(context: PlatformContext)
    fun onHistoryClick()
}
