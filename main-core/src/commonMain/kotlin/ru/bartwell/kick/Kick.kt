package ru.bartwell.kick

import ru.bartwell.kick.core.data.Module
import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.core.data.StartScreen
import ru.bartwell.kick.core.data.Theme

public interface Kick {
    public var theme: Theme
    public val modules: List<Module>
    public fun launch(context: PlatformContext, startScreen: StartScreen? = null)
    public fun getShortcutId(): String

    public companion object Companion {
        internal var instance: Kick? = null
        public var theme: Theme
            get() = instance?.theme ?: Theme.Auto
            set(value) {
                instance?.theme = value
            }
        public val modules: List<Module>
            get() = instance?.modules ?: emptyList()

        public fun init(impl: Kick) {
            this.instance = impl
        }

        public fun launch(context: PlatformContext, startScreen: StartScreen? = null) {
            instance?.launch(context, startScreen)
        }

        public fun getShortcutId(): String = instance?.getShortcutId() ?: ""
    }

    public class Configuration {
        public val modules: MutableList<Module> = mutableListOf()
        public var theme: Theme = Theme.Auto
        public var enableShortcut: Boolean = true

        public fun module(module: Module) {
            modules += module
        }

        public fun modules(list: List<Module>) {
            modules += list
        }
    }
}
