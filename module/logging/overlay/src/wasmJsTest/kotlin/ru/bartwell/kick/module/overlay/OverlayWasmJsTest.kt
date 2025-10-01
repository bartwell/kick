package ru.bartwell.kick.module.overlay

import kotlinx.browser.document
import org.w3c.dom.HTMLElement
import ru.bartwell.kick.core.data.getPlatformContext
import ru.bartwell.kick.module.overlay.core.overlay.KickOverlay
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@Suppress("FunctionNaming")
private const val OVERLAY_ELEMENT_ID = "kick-overlay"

@Suppress("FunctionNaming")
class OverlayWasmJsTest {
    @Test
    fun showHide_addsAndRemovesDomNode() {
        // Ensure clean state
        (document.getElementById(OVERLAY_ELEMENT_ID) as? HTMLElement)?.let { it.parentElement?.removeChild(it) }

        val ctx = getPlatformContext()
        KickOverlay.init(ctx)
        KickOverlay.show(ctx)

        val el = document.getElementById(OVERLAY_ELEMENT_ID) as? HTMLElement
        assertNotNull(el)
        assertEquals(OVERLAY_ELEMENT_ID, el.id)

        KickOverlay.hide()
        val removed = document.getElementById(OVERLAY_ELEMENT_ID)
        assertNull(removed)
    }
}
