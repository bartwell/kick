package ru.bartwell.kick.module.multiplatformsettings.feature

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import ru.bartwell.kick.core.presentation.AppUiEnvironment
import ru.bartwell.kick.core.presentation.LocalAppUiEnvironment
import ru.bartwell.kick.module.multiplatformsettings.feature.editor.presentation.SettingsEditorContent
import ru.bartwell.kick.module.multiplatformsettings.feature.list.presentation.SettingsListContent

@Suppress("FunctionNaming")
@RunWith(AndroidJUnit4::class)
class SettingsAndroidUiTest {
    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun list_click_storage_and_back() {
        val fake = FakeSettingsListComponent(listOf("s1", "s2"))
        compose.setContent { SettingsListContentWithEnvironment(component = fake) }
        compose.onNodeWithTag("storage_item_s2").performClick()
        assertEquals("s2", fake.clicked)
        compose.onNodeWithContentDescription("Back").performClick()
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

@Composable
private fun SettingsListContentWithEnvironment(component: FakeSettingsListComponent) {
    CompositionLocalProvider(
        LocalAppUiEnvironment provides AppUiEnvironment(
            screenCloser = {},
            canNavigateBack = true,
        )
    ) {
        SettingsListContent(component = component)
    }
}
