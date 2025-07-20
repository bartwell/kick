package ru.bartwell.kick

import platform.UIKit.UISceneConfiguration
import platform.UIKit.UISceneSession

public object ShortcutActionHandler {

    public fun getConfiguration(session: UISceneSession): UISceneConfiguration {
        return UISceneConfiguration(
            name = session.configuration.name,
            sessionRole = session.role
        )
    }
}
