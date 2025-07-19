package ru.bartwell.kick.core.util

import ru.bartwell.kick.core.data.Platform

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
public actual object PlatformUtils {
    public actual fun getPlatform(): Platform = Platform.DESKTOP
}
