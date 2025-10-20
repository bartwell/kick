package ru.bartwell.kick.module.overlay.core.overlay

import kotlinx.browser.document
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLSpanElement
import org.w3c.dom.events.Event
import org.w3c.dom.events.MouseEvent
import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.module.overlay.core.persists.OverlaySettings
import ru.bartwell.kick.module.overlay.core.store.OverlayStore

private const val INITIAL_WINDOW_X_PX = 50
private const val INITIAL_WINDOW_Y_PX = 200

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
public actual object KickOverlay {
    private var overlayRoot: HTMLElement? = null
    private var overlayLines: HTMLElement? = null
    private var mouseMoveListener: ((Event) -> Unit)? = null
    private var mouseUpListener: ((Event) -> Unit)? = null
    private var scope: CoroutineScope? = null
    private var itemsJob: Job? = null

    @Suppress("EmptyFunctionBlock")
    public actual fun init(context: PlatformContext) {}

    @Suppress("LongMethod", "StringLiteralDuplication")
    public actual fun show() {
        if (overlayRoot != null) {
            overlayRoot!!.style.display = ""
            overlayRoot!!.style.visibility = "visible"
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
                setProperty("pointer-events", "auto")
                setProperty("user-select", "none")
                display = "inline-block"
                // Auto-size to content
                width = "max-content"
                height = "max-content"
            }
        }

        val container = (document.createElement("div") as HTMLElement).apply {
            id = "kick-overlay-content"
            style.apply {
                position = "relative"
                backgroundColor = "white"
                setProperty("border", "1px solid rgba(0,0,0,0.12)")
                setProperty("box-shadow", "0 6px 24px rgba(0,0,0,0.15)")
                borderRadius = "0px"
                padding = "2px 16px 2px 6px"
                color = "black"
                fontFamily = "-apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, " +
                    "Ubuntu, Cantarell, 'Helvetica Neue', Arial, sans-serif"
                fontSize = "12px"
                setProperty("line-height", "16px")
                setProperty("white-space", "nowrap")
            }
        }

        val close = (document.createElement("span") as HTMLSpanElement).apply {
            id = "kick-overlay-close"
            textContent = "\u00D7"
            style.apply {
                position = "absolute"
                right = "2px"
                top = "2px"
                width = "20px"
                height = "20px"
                textAlign = "center"
                cursor = "pointer"
                borderRadius = "10px"
                setProperty("line-height", "16px")
                setProperty("user-select", "none")
            }
            addEventListener("click") { _ -> onCloseClicked() }
            addEventListener("mousedown") { e -> e.stopPropagation() }
        }

        val lines = (document.createElement("div") as HTMLElement).apply {
            id = "kick-overlay-lines"
            style.apply { setProperty("white-space", "nowrap") }
        }

        container.appendChild(close)
        container.appendChild(lines)
        root.appendChild(container)
        document.body?.appendChild(root)

        overlayRoot = root
        overlayLines = lines

        // Drag handling
        var dragging = false
        var startX = 0.0
        var startY = 0.0
        var offsetX = 0.0
        var offsetY = 0.0

        val onMouseDown: (MouseEvent) -> Unit = { ev ->
            dragging = true
            startX = ev.clientX.toDouble()
            startY = ev.clientY.toDouble()
            offsetX = root.offsetLeft.toDouble()
            offsetY = root.offsetTop.toDouble()
            ev.preventDefault()
        }
        val onMouseMove: (MouseEvent) -> Unit = { ev ->
            if (dragging) {
                val dx = ev.clientX - startX
                val dy = ev.clientY - startY
                root.style.left = "${(offsetX + dx).toInt()}px"
                root.style.top = "${(offsetY + dy).toInt()}px"
            }
        }
        val onMouseUp: (MouseEvent) -> Unit = { _ -> dragging = false }

        root.addEventListener("mousedown") { e: Event -> onMouseDown(e as MouseEvent) }
        val moveListener: (Event) -> Unit = { e -> onMouseMove(e as MouseEvent) }
        val upListener: (Event) -> Unit = { e -> onMouseUp(e as MouseEvent) }
        document.addEventListener("mousemove", moveListener)
        document.addEventListener("mouseup", upListener)
        mouseMoveListener = moveListener
        mouseUpListener = upListener

        // Subscribe to data updates
        scope = CoroutineScope(Dispatchers.Default).also { s ->
            itemsJob = s.launch {
                OverlayStore.items.collect { currentItems ->
                    renderLines(currentItems)
                }
            }
        }

        // Render initial state
        renderLines(OverlayStore.items.value)
    }

    public actual fun hide() {
        OverlaySettings.setEnabled(false)

        itemsJob?.cancel()
        itemsJob = null
        scope?.cancel()
        scope = null

        mouseMoveListener?.let { document.removeEventListener("mousemove", it) }
        mouseUpListener?.let { document.removeEventListener("mouseup", it) }
        mouseMoveListener = null
        mouseUpListener = null

        overlayRoot?.let { el -> el.parentElement?.removeChild(el) }
        overlayRoot = null
        overlayLines = null
    }

    private fun renderLines(items: List<Pair<String, String>>) {
        val container = overlayLines ?: return
        while (container.firstChild != null) {
            container.removeChild(container.firstChild!!)
        }
        for ((key, value) in items) {
            val line = document.createElement("div") as HTMLElement
            line.textContent = "$key: $value"
            line.style.apply {
                setProperty("white-space", "nowrap")
                setProperty("margin", "2px 0")
                setProperty("overflow", "hidden")
                setProperty("text-overflow", "clip")
            }
            container.appendChild(line)
        }
    }

    private fun onCloseClicked() {
        hide()
    }
}
