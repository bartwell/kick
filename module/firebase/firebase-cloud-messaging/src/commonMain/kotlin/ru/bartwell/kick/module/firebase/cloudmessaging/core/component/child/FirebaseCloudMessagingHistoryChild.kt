package ru.bartwell.kick.module.firebase.cloudmessaging.core.component.child

import ru.bartwell.kick.core.component.Child
import ru.bartwell.kick.module.firebase.cloudmessaging.feature.history.presentation.FirebaseCloudMessagingHistoryComponent

internal data class FirebaseCloudMessagingHistoryChild(
    override val component: FirebaseCloudMessagingHistoryComponent,
) : Child<FirebaseCloudMessagingHistoryComponent>
