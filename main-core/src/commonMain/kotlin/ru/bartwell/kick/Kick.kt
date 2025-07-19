package ru.bartwell.kick

import ru.bartwell.kick.core.data.Module
import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.core.data.Theme

public interface Kick {
    public var theme: Theme
    public fun launch(context: PlatformContext)
    public fun getShortcutId(): String

    public companion object Companion {
        internal lateinit var impl: Kick
        public var theme: Theme
            get() = impl.theme
            set(value) { impl.theme = value }

        public fun init(impl: Kick) {
            this.impl = impl
        }

        @Suppress("OptionalUnit")
        public fun launch(context: PlatformContext): Unit = impl.launch(context)
        public fun getShortcutId(): String = impl.getShortcutId()
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
