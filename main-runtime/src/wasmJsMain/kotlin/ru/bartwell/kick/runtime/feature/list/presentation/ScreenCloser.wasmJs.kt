package ru.bartwell.kick.runtime.feature.list.presentation

import androidx.compose.runtime.Composable
import kotlinx.browser.document
import ru.bartwell.kick.core.util.WindowStateManager
import ru.bartwell.kick.runtime.core.util.LocalOverlayRoot

@Composable
internal actual fun screenCloser(): () -> Unit {
    val root = LocalOverlayRoot.current
    return {
        val el = root ?: document.getElementById("kick-viewer")
        el?.parentElement?.removeChild(el)
        WindowStateManager.getInstance()?.setWindowClosed()
    }
}
