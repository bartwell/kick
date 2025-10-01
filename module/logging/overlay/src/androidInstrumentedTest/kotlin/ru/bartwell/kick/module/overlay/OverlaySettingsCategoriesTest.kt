package ru.bartwell.kick.module.overlay

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import ru.bartwell.kick.core.data.toPlatformContext
import ru.bartwell.kick.module.overlay.core.persists.OverlaySettings
import ru.bartwell.kick.module.overlay.core.store.DEFAULT_CATEGORY
import ru.bartwell.kick.module.overlay.core.store.OverlayStore
import ru.bartwell.kick.module.overlay.feature.settings.presentation.DefaultOverlayComponent
import ru.bartwell.kick.module.overlay.feature.settings.presentation.OverlayContent

@RunWith(AndroidJUnit4::class)
@Suppress("FunctionNaming")
class OverlaySettingsCategoriesTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Before
    fun setup() {
        // Reset store and settings
        OverlayStore.clear()
        OverlayStore.selectCategory(DEFAULT_CATEGORY)
        val ctx = composeRule.activity.applicationContext.toPlatformContext()
        OverlaySettings(ctx)
        OverlaySettings.setSelectedCategory(DEFAULT_CATEGORY)
    }

    @Test
    fun category_switches_list_and_persists_selection() {
        // Prepare data for two categories
        OverlayStore.set("a", "1") // Default
        OverlayStore.set("fps", "60", "Perf")

        lateinit var component: DefaultOverlayComponent
        composeRule.activityRule.scenario.onActivity { activity ->
            component = DefaultOverlayComponent(
                componentContext = DefaultComponentContext(LifecycleRegistry()),
                onEnabledChangeCallback = {},
                onBackCallback = {}
            )
            activity.setContent { OverlayContent(component = component) }
        }

        // Default selected: shows only Default items
        composeRule.onNodeWithText("a: 1").assertIsDisplayed()
        // Open dropdown and select Perf
        composeRule.onNodeWithText(DEFAULT_CATEGORY).performClick()
        composeRule.onNodeWithText("Perf").performClick()

        // Perf selected: shows only Perf items
        composeRule.onNodeWithText("fps: 60").assertIsDisplayed()

        // Verify persisted selection
        assert(OverlaySettings.getSelectedCategory() == "Perf")
    }
}
