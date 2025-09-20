package ru.bartwell.kick.runtime.core.util

import ru.bartwell.kick.core.data.Module
import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.core.data.StartScreen

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
internal expect object LaunchManager {
    fun launch(
        context: PlatformContext,
        modules: List<Module>,
        startScreen: StartScreen? = null,
    )
}
