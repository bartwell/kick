package ru.bartwell.kick.module.sqlite.runtime.feature

import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import org.junit.Rule
import org.junit.Test
import ru.bartwell.kick.module.sqlite.runtime.feature.update.presentation.FakeUpdateComponent
import ru.bartwell.kick.module.sqlite.runtime.feature.update.presentation.UpdateContent
import ru.bartwell.kick.module.sqlite.runtime.feature.viewer.presentation.FakeViewerComponent
import ru.bartwell.kick.module.sqlite.runtime.feature.viewer.presentation.ViewerContent
import kotlin.test.assertTrue

@Suppress("FunctionNaming")
class RuntimeUiTest {
    @get:Rule val compose = createComposeRule()

    @Test
    fun viewer_menu_delete_and_cancel() {
        val fake = FakeViewerComponent()
        compose.setContent { ViewerContent(component = fake) }

        compose.onNodeWithTag("menu_button").performClick()
        compose.onNodeWithTag("menu_delete").performClick()
        // In delete mode
        compose.onNodeWithTag("confirm_delete").assertExists()
        compose.onNodeWithTag("cancel_delete").performClick()
        // Back to normal mode
        compose.onNodeWithTag("menu_button").assertExists()
    }

    @Test
    fun viewer_select_row_and_confirm() {
        val fake = FakeViewerComponent()
        compose.setContent { ViewerContent(component = fake) }
        compose.onNodeWithTag("menu_button").performClick()
        compose.onNodeWithTag("menu_delete").performClick()
        compose.waitForIdle()
        compose.onNodeWithTag("confirm_delete").assertExists()
        compose.onNodeWithTag("confirm_delete").performClick()
        assertTrue(fake.confirmInvoked)
    }

    @Test
    fun update_value_save_and_null_toggle() {
        val fake = FakeUpdateComponent()
        compose.setContent { UpdateContent(component = fake) }
        compose.onNodeWithTag("update_value").performTextInput("XYZ")
        compose.onNodeWithTag("null_checkbox").performClick()
        compose.onNodeWithTag("update_value").assertIsNotEnabled()
        // Save action
        compose.onNodeWithText("Save").performClick()
        assertTrue(fake.saveInvoked)
    }
}
