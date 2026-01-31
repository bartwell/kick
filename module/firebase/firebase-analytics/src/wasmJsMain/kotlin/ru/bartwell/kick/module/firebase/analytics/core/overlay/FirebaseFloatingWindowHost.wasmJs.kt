package ru.bartwell.kick.module.firebase.analytics.core.overlay

import ru.bartwell.kick.core.data.PlatformContext

internal actual object FirebaseFloatingWindowHost {
    actual fun init(context: PlatformContext) = Unit
    actual fun setVisible(enabled: Boolean) = Unit
}
