package ru.bartwell.kick.runtime

import ru.bartwell.kick.Kick
import ru.bartwell.kick.core.component.Config
import ru.bartwell.kick.core.data.Module
import ru.bartwell.kick.core.data.ModuleDescription
import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.core.data.Theme

internal class EmptyKickImpl : Kick {

    override var theme: Theme = Theme.Auto
    override val modules: List<Module> = emptyList()

    override fun launch(context: PlatformContext, description: ModuleDescription?, config: Config?) {
        println(
            "Kick: It appears you’re attempting to run Kick, but a stub module has been added. " +
                "Please ensure that both the `main-core` and `main-runtime` modules are correctly configured"
        )
    }

    override fun getShortcutId(): String = ""
}
