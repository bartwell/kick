package ru.bartwell.kick.runtime

import ru.bartwell.kick.Kick
import ru.bartwell.kick.core.data.Module
import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.core.data.Theme
import ru.bartwell.kick.runtime.core.util.LaunchManager
import ru.bartwell.kick.runtime.core.util.ShortcutManager
import ru.bartwell.kick.runtime.core.util.id

internal class KickImpl(
    context: PlatformContext,
    isShortcutEnabled: Boolean = true,
    private var modules: List<Module> = emptyList(),
    override var theme: Theme = Theme.Auto,
) : Kick {

    init {
        if (isShortcutEnabled) {
            ShortcutManager.setup(context)
        }
    }

    @Suppress("OptionalUnit")
    override fun launch(context: PlatformContext): Unit =
        LaunchManager.launch(context, modules)

    override fun getShortcutId(): String = ShortcutManager.id
}

public fun Kick.Companion.init(context: PlatformContext, block: Kick.Configuration.() -> Unit) {
    val configuration = Kick.Configuration().apply(block)
    init(
        KickImpl(
            theme = configuration.theme,
            modules = configuration.modules,
            isShortcutEnabled = configuration.enableShortcut,
            context = context,
        )
    )
}
