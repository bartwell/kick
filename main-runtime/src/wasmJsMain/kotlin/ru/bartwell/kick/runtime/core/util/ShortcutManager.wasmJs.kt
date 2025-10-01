package ru.bartwell.kick.runtime.core.util

import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.HTMLElement
import org.w3c.dom.events.Event
import org.w3c.dom.events.KeyboardEvent
import ru.bartwell.kick.Kick
import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.core.util.WindowStateManager

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
internal actual object ShortcutManager {

    private var listener: ((Event) -> Unit)? = null
    private var buttonEl: HTMLElement? = null

    private const val BUTTON_ID = "kick-shortcut-button"
    private val FONT_STACK: String = listOf(
        "system-ui",
        "-apple-system",
        "Segoe UI",
        "Roboto",
        "Ubuntu",
        "Cantarell",
        "Noto Sans",
        "Arial",
        "sans-serif"
    ).joinToString(", ")

    internal actual fun setup(context: PlatformContext) {
        if (listener == null) {
            val l: (Event) -> Unit = { ev ->
                if (shouldOpenWithKeyboard(ev) && WindowStateManager.getInstance()?.isWindowOpen() != true) {
                    Kick.launch(context)
                }
            }
            window.addEventListener("keydown", l)
            listener = l
        }

        if (buttonEl == null && document.getElementById(BUTTON_ID) == null) {
            createButton(context)
        }
    }

    private fun shouldOpenWithKeyboard(ev: Event): Boolean {
        val e = ev as? KeyboardEvent ?: return false
        if (e.type != "keydown") return false
        val key = (e.key ?: "").lowercase()
        val metaOrCtrl = e.metaKey || e.ctrlKey
        return key == "k" && metaOrCtrl && e.altKey && e.shiftKey
    }

    private fun createButton(context: PlatformContext) {
        val btn = (document.createElement("div") as HTMLElement).apply {
            id = BUTTON_ID
            this.title = ShortcutManager.subtitle
            style.apply {
                position = "fixed"
                left = "12px"
                bottom = "12px"
                width = "28px"
                height = "28px"
                display = "flex"
                justifyContent = "center"
                alignItems = "center"
                borderRadius = "6px"
                backgroundColor = "rgba(0,0,0,0.65)"
                color = "#fff"
                fontFamily = FONT_STACK
                fontWeight = "700"
                fontSize = "14px"
                cursor = "pointer"
                zIndex = "2147483645"
                boxShadow = "0 2px 6px rgba(0,0,0,0.4)"
            }
            textContent = "K"
            addEventListener("click", {
                if (WindowStateManager.getInstance()?.isWindowOpen() != true) {
                    Kick.launch(context)
                }
            })
        }
        document.body?.appendChild(btn)
        buttonEl = btn
    }
}
