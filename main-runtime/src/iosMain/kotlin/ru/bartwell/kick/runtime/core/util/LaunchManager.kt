package ru.bartwell.kick.runtime.core.util

import ru.bartwell.kick.core.data.Module
import ru.bartwell.kick.core.data.PlatformContext

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
internal actual object LaunchManager {
    actual fun launch(context: PlatformContext, modules: List<Module>) = IosSceneController.present(modules)
}
