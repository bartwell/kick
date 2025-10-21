package ru.bartwell.kick.module.firebase.cloudmessaging.core.data

/**
 * Aggregated notification related information that can be fetched from the host application.
 */
public data class FirebaseNotificationStatus(
    val iosPermission: IosNotificationPermissionStatus? = null,
    val androidChannel: AndroidNotificationChannelStatus? = null,
    val isGooglePlayServicesAvailable: Boolean? = null,
)

public enum class IosNotificationPermissionStatus(public val description: String) {
    NotDetermined("Not determined"),
    Denied("Denied"),
    Authorized("Authorized"),
    Provisional("Provisional"),
    Ephemeral("Ephemeral"),
    Unknown("Unknown"),
}

public data class AndroidNotificationChannelStatus(
    val id: String,
    val name: String? = null,
    val description: String? = null,
    val importance: String? = null,
    val isEnabled: Boolean? = null,
    val isAppNotificationsEnabled: Boolean? = null,
)
