package ru.bartwell.kick.module.sqlite.runtime.feature

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import ru.bartwell.kick.module.sqlite.runtime.feature.update.presentation.UpdateContent
import ru.bartwell.kick.module.sqlite.runtime.feature.viewer.presentation.ViewerContent

@Suppress("FunctionNaming")
@RunWith(AndroidJUnit4::class)
class SqliteRuntimeAndroidUiTest {
    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun viewer_delete_and_select_confirm() {
        val fake = FakeViewerComponent()
        compose.setContent { ViewerContent(component = fake) }
        compose.onNodeWithTag("menu_button").performClick()
        compose.onNodeWithTag("menu_delete").performClick()
        compose.onNodeWithTag("confirm_delete").performClick()
        assertTrue(fake.confirmInvoked)
    }

    @Test
    fun update_value_and_null_save() {
        val fake = FakeUpdateComponent()
        compose.setContent { UpdateContent(component = fake) }
        compose.onNodeWithTag("update_value").performTextInput("XYZ")
        compose.onNodeWithTag("null_checkbox").performClick()
        compose.onNodeWithText("Save").performClick()
        assertTrue(fake.saveInvoked)
    }
}
