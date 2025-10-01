package ru.bartwell.kick.module.overlay.core.overlay

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document
import org.w3c.dom.HTMLElement
import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.module.overlay.core.persists.OverlaySettings

private const val INITIAL_WINDOW_X_PX = 50
private const val INITIAL_WINDOW_Y_PX = 200

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
public actual object KickOverlay {
    private var overlayRoot: HTMLElement? = null

    @Suppress("EmptyFunctionBlock")
    public actual fun init(context: PlatformContext) {}

    @OptIn(ExperimentalComposeUiApi::class)
    public actual fun show(context: PlatformContext) {
        val existing = overlayRoot
        if (existing != null) {
            existing.style.display = ""
            existing.style.visibility = "visible"
            OverlaySettings.setEnabled(true)
            return
        }

        OverlaySettings.setEnabled(true)

        val root = (document.createElement("div") as HTMLElement).apply {
            id = "kick-overlay"
            style.apply {
                position = "fixed"
                top = "${INITIAL_WINDOW_Y_PX}px"
                left = "${INITIAL_WINDOW_X_PX}px"
                zIndex = "2147483647"
                width = "180px"
                height = "60px"
                backgroundColor = "transparent"
                borderRadius = "0px"
                setProperty("pointer-events", "auto")
            }
        }
        document.body?.appendChild(root)
        overlayRoot = root

        var dragging = false
        var startX = 0.0
        var startY = 0.0
        var offsetX = 0.0
        var offsetY = 0.0

        val onMouseDown: (org.w3c.dom.events.MouseEvent) -> Unit = { ev ->
            dragging = true
            startX = ev.clientX.toDouble()
            startY = ev.clientY.toDouble()
            offsetX = root.offsetLeft.toDouble()
            offsetY = root.offsetTop.toDouble()
            ev.preventDefault()
        }
        val onMouseMove: (org.w3c.dom.events.MouseEvent) -> Unit = { ev ->
            if (dragging) {
                val dx = ev.clientX - startX
                val dy = ev.clientY - startY
                root.style.left = "${(offsetX + dx).toInt()}px"
                root.style.top = "${(offsetY + dy).toInt()}px"
            }
        }
        val onMouseUp: (org.w3c.dom.events.MouseEvent) -> Unit = { _ -> dragging = false }

        root.addEventListener("mousedown") { e: org.w3c.dom.events.Event ->
            onMouseDown(e as org.w3c.dom.events.MouseEvent)
        }
        document.addEventListener("mousemove") { e: org.w3c.dom.events.Event ->
            onMouseMove(e as org.w3c.dom.events.MouseEvent)
        }
        document.addEventListener("mouseup") { e: org.w3c.dom.events.Event ->
            onMouseUp(e as org.w3c.dom.events.MouseEvent)
        }

        ComposeViewport(root) {
            Overlay(
                root = root,
                onCloseClick = ::onCloseClicked,
            )
        }
    }

    public actual fun hide() {
        OverlaySettings.setEnabled(false)
        overlayRoot?.let { el ->
            el.parentElement?.removeChild(el)
        }
        overlayRoot = null
    }

    private fun onCloseClicked() {
        hide()
    }
}
