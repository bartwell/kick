package ru.bartwell.kick.module.multiplatformsettings

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import org.junit.Rule
import org.junit.Test
import ru.bartwell.kick.module.multiplatformsettings.feature.editor.presentation.FakeSettingsEditorComponent
import ru.bartwell.kick.module.multiplatformsettings.feature.editor.presentation.SettingsEditorContent
import ru.bartwell.kick.module.multiplatformsettings.feature.list.presentation.FakeSettingsListComponent
import ru.bartwell.kick.module.multiplatformsettings.feature.list.presentation.SettingsListContent
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@Suppress("FunctionNaming")
class UiTest {
    @get:Rule val compose = createComposeRule()

    @Test
    fun list_click_storage_and_back() {
        val fake = FakeSettingsListComponent(listOf("s1", "s2"))
        compose.setContent { SettingsListContent(component = fake) }
        compose.onNodeWithTag("storage_item_s2").performClick()
        assertEquals("s2", fake.clicked)
        compose.onNodeWithTag("back").performClick()
        assertTrue(fake.backInvoked)
    }

    @Test
    fun editor_edit_save_and_delete() {
        val fake = FakeSettingsEditorComponent()
        compose.setContent { SettingsEditorContent(component = fake) }
        compose.onNodeWithTag("entry_a").performTextInput("9")
        compose.onNodeWithTag("delete_b").performClick()
        compose.onNodeWithTag("save").performClick()
        assertTrue(fake.saveInvoked)
        assertTrue(fake.deleted.contains("b"))
        compose.onNodeWithTag("back").performClick()
        assertTrue(fake.backInvoked)
    }
}
