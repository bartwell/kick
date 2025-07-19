package ru.bartwell.kick.runtime

import kotlinx.cinterop.BetaInteropApi
import platform.UIKit.UISceneConfiguration
import platform.UIKit.UISceneSession

public object ShortcutActionHandler {

    @OptIn(BetaInteropApi::class)
    public fun getConfiguration(session: UISceneSession): UISceneConfiguration {
        val configuration = UISceneConfiguration(
            name = session.configuration.name,
            sessionRole = session.role,
        )
        configuration.delegateClass = KickSceneDelegate().`class`()
        return configuration
    }
}
