package ru.bartwell.kick.module.firebase.cloudmessaging.feature.main.presentation

internal data class FirebaseCloudMessagingState(
    val isFirebaseAvailable: Boolean = false,
    val token: String? = null,
    val tokenError: String? = null,
    val isTokenLoading: Boolean = false,
    val firebaseId: String? = null,
    val firebaseIdError: String? = null,
    val isFirebaseIdLoading: Boolean = false,
    val availabilityMessage: String? = null,
)
