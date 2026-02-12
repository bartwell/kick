package ru.bartwell.kick.sample.plugin

import ru.bartwell.kick.Kick

/**
 * Minimal entry point to verify Kick plugin dependencies resolve and compile.
 */
object App {
    fun run() {
        // Reference Kick so that main-core (and plugin-added deps) are compiled
        val modules = Kick.modules
    }
}
