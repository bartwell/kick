package ru.bartwell.kick.module.firebase.cloudmessaging.feature.detail.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import ru.bartwell.kick.module.firebase.cloudmessaging.core.data.FirebaseMessage

internal class DefaultFirebaseCloudMessagingDetailsComponent(
    componentContext: ComponentContext,
    message: FirebaseMessage,
    private val onFinished: () -> Unit,
) : FirebaseCloudMessagingDetailsComponent, ComponentContext by componentContext {
    private val _state = MutableValue(FirebaseCloudMessagingDetailsState(message))
    override val state: Value<FirebaseCloudMessagingDetailsState> = _state

    override fun onBackPressed() {
        onFinished()
    }
}
