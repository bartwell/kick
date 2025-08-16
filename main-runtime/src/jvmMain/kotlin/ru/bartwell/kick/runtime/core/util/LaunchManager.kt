package ru.bartwell.kick.runtime.core.util

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.awt.ComposeWindow
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import ru.bartwell.kick.core.data.Module
import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.core.data.StartScreen
import ru.bartwell.kick.runtime.App
import ru.bartwell.kick.runtime.core.component.DefaultRootComponent
import java.awt.Dimension

private const val WINDOW_WIDTH = 800
private const val WINDOW_HEIGHT = 600
private const val MIN_WINDOW_SIZE = 400

internal val LocalComposeWindow = staticCompositionLocalOf<ComposeWindow?> { null }

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
internal actual object LaunchManager {
    actual fun launch(
        context: PlatformContext,
        modules: List<Module>,
        startScreen: StartScreen?,
    ) {
        val lifecycle = LifecycleRegistry()
        val componentContext = DefaultComponentContext(lifecycle)
        val rootComponent = DefaultRootComponent(
            componentContext = componentContext,
            modules = modules,
            startScreen = startScreen,
        )

        val window = ComposeWindow().apply {
            title = "Viewer"
            size = Dimension(WINDOW_WIDTH, WINDOW_HEIGHT)
            minimumSize = Dimension(MIN_WINDOW_SIZE, MIN_WINDOW_SIZE)
            preferredSize = Dimension(WINDOW_WIDTH, WINDOW_HEIGHT)
            setLocationRelativeTo(null)
            setContent {
                CompositionLocalProvider(LocalComposeWindow provides window) {
                    App(rootComponent)
                }
            }
        }
        window.pack()
        window.isVisible = true
    }
}
