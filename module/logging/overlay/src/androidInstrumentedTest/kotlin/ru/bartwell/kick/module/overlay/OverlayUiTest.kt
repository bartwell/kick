package ru.bartwell.kick.module.overlay

import android.app.Application
import android.view.View
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import ru.bartwell.kick.core.data.toPlatformContext
import ru.bartwell.kick.module.overlay.core.overlay.KickOverlay
import ru.bartwell.kick.module.overlay.core.persists.OverlaySettings
import ru.bartwell.kick.module.overlay.feature.settings.presentation.DefaultOverlayComponent
import ru.bartwell.kick.module.overlay.feature.settings.presentation.OverlayContent

private const val TAG_OVERLAY = "KickOverlayInApp"
private const val TAG_PANEL = "KickOverlayPanel"
private const val WAIT_TIMEOUT_MS = 5_000L
private const val WAIT_STEP_MS = 50L

@Suppress("FunctionNaming")
@RunWith(AndroidJUnit4::class)
class OverlayUiTest {

    @Before
    fun before() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as Application
        val platformContext = appContext.toPlatformContext()
        // Ensure clean state before every test
        OverlaySettings(platformContext)
        OverlaySettings.setEnabled(false)
        KickOverlay.hide()
    }

    private fun waitForViewWithTag(
        activity: ComponentActivity,
        tag: String,
        expectPresent: Boolean,
        timeoutMs: Long = WAIT_TIMEOUT_MS,
    ) {
        val start = System.currentTimeMillis()
        while (System.currentTimeMillis() - start < timeoutMs) {
            val root = activity.window?.decorView as? ViewGroup
            val view = root?.findViewWithTag<View>(tag)
            if (expectPresent && view != null) return
            if (!expectPresent && view == null) return
            Thread.sleep(WAIT_STEP_MS)
        }
        val root = activity.window?.decorView as? ViewGroup
        val view = root?.findViewWithTag<View>(tag)
        if (expectPresent) assertNotNull(view) else assertNull(view)
    }

    @After
    fun tearDown() {
        // Ensure overlay is hidden after tests
        KickOverlay.hide()
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
    }

    @Test
    fun overlayWindow_showsAndHides_viaComponent() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as Application
        val platformContext = appContext.toPlatformContext()
        // Register lifecycle callbacks before activity launch
        KickOverlay.init(platformContext)

        lateinit var component: DefaultOverlayComponent

        ActivityScenario.launch(ComponentActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                component = DefaultOverlayComponent(
                    componentContext = DefaultComponentContext(LifecycleRegistry()),
                    onEnabledChangeCallback = { enabled ->
                        if (enabled) KickOverlay.show() else KickOverlay.hide()
                    },
                    onBackCallback = {},
                )
                activity.setContent { OverlayContent(component = component) }
            }

            scenario.onActivity { activity ->
                // Initially hidden
                waitForViewWithTag(activity, tag = TAG_OVERLAY, expectPresent = false)
                // Enable and verify appears
                component.onEnabledChange(platformContext, true)
                waitForViewWithTag(activity, tag = TAG_OVERLAY, expectPresent = true)
                waitForViewWithTag(activity, tag = TAG_PANEL, expectPresent = true)
                // Disable and verify disappears
                component.onEnabledChange(platformContext, false)
                waitForViewWithTag(activity, tag = TAG_PANEL, expectPresent = false)
                waitForViewWithTag(activity, tag = TAG_OVERLAY, expectPresent = false)
            }
        }
    }

    @Test
    fun overlaySettings_togglePersists() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as Application
        val platformContext = appContext.toPlatformContext()
        OverlaySettings(platformContext)
        OverlaySettings.setEnabled(false)
        OverlaySettings.setEnabled(true)
        assert(OverlaySettings.isEnabled())
        OverlaySettings.setEnabled(false)
        assert(!OverlaySettings.isEnabled())
    }
}
