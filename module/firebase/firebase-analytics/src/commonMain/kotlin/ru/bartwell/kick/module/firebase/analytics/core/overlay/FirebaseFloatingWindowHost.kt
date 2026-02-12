package ru.bartwell.kick.module.firebase.analytics.core.overlay

import ru.bartwell.kick.core.data.PlatformContext

internal expect object FirebaseFloatingWindowHost {
    fun init(context: PlatformContext)
    fun setVisible(enabled: Boolean)
}
