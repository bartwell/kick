package ru.bartwell.kick.core.util

import ru.bartwell.kick.core.data.Platform

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
public expect object PlatformUtils {
    public fun getPlatform(): Platform
}
