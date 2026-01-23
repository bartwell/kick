package ru.bartwell.kick.runtime

import ru.bartwell.kick.Kick
import ru.bartwell.kick.core.data.Module
import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.core.data.StartScreen
import ru.bartwell.kick.core.data.Theme

internal class EmptyKickImpl : Kick {

    override var theme: Theme = Theme.Auto
    override val modules: List<Module> = emptyList()

    override fun launch(context: PlatformContext) {
        launch(context, null)
    }

    override fun launch(context: PlatformContext, startScreen: StartScreen?) {
        println(
            "Kick: It appears you’re attempting to run Kick, but a stub module has been added. " +
                "Please ensure that both the `main-core` and `main-runtime` modules are correctly configured"
        )
    }

    override fun close() {
        println(
            "Kick: Unable to close the viewer because a stub module has been added. " +
                "Please ensure that both the `main-core` and `main-runtime` modules are correctly configured"
        )
    }

    override fun getShortcutId(): String = ""
}
