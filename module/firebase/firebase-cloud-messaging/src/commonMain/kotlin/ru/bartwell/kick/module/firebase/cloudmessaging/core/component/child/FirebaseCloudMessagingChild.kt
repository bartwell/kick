package ru.bartwell.kick.module.firebase.cloudmessaging.core.component.child

import ru.bartwell.kick.core.component.Child
import ru.bartwell.kick.module.firebase.cloudmessaging.feature.presentation.FirebaseCloudMessagingComponent

internal data class FirebaseCloudMessagingChild(
    override val component: FirebaseCloudMessagingComponent,
) : Child<FirebaseCloudMessagingComponent>
