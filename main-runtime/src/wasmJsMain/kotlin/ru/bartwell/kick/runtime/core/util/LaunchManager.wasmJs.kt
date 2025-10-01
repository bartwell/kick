package ru.bartwell.kick.runtime.core.util

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import kotlinx.browser.document
import org.w3c.dom.HTMLElement
import ru.bartwell.kick.core.data.Module
import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.core.data.StartScreen
import ru.bartwell.kick.core.util.WindowStateManager
import ru.bartwell.kick.runtime.App
import ru.bartwell.kick.runtime.core.component.DefaultRootComponent

private const val ROOT_ID = "kick-viewer"

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
internal actual object LaunchManager {
    @OptIn(ExperimentalComposeUiApi::class)
    actual fun launch(
        context: PlatformContext,
        modules: List<Module>,
        startScreen: StartScreen?,
    ) {
        if (document.getElementById(ROOT_ID) != null) return

        val root = (document.createElement("div") as HTMLElement).apply {
            id = ROOT_ID
            style.apply {
                position = "fixed"
                left = "0"
                top = "0"
                width = "100vw"
                height = "100vh"
                zIndex = "2147483646"
                backgroundColor = "rgba(0,0,0,0.25)"
            }
        }
        document.body?.appendChild(root)
        WindowStateManager.getInstance()?.setWindowOpen()

        val lifecycle = LifecycleRegistry()
        val componentContext = DefaultComponentContext(lifecycle)
        val rootComponent = DefaultRootComponent(
            componentContext = componentContext,
            modules = modules,
            startScreen = startScreen,
        )

        ComposeViewport(root) {
            CompositionLocalProvider(LocalOverlayRoot provides root) {
                App(rootComponent)
            }
        }
    }
}

internal val LocalOverlayRoot = androidx.compose.runtime.staticCompositionLocalOf<HTMLElement?> { null }
