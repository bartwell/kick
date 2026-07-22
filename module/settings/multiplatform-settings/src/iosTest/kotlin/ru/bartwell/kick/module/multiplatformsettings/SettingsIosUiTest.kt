package ru.bartwell.kick.module.multiplatformsettings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.runComposeUiTest
import ru.bartwell.kick.core.presentation.AppUiEnvironment
import ru.bartwell.kick.core.presentation.LocalAppUiEnvironment
import ru.bartwell.kick.module.multiplatformsettings.feature.editor.presentation.FakeSettingsEditorComponent
import ru.bartwell.kick.module.multiplatformsettings.feature.editor.presentation.SettingsEditorContent
import ru.bartwell.kick.module.multiplatformsettings.feature.list.presentation.FakeSettingsListComponent
import ru.bartwell.kick.module.multiplatformsettings.feature.list.presentation.SettingsListContent
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@Suppress("FunctionNaming")
@OptIn(ExperimentalTestApi::class)
class SettingsIosUiTest {
    @Test
    fun list_and_editor() = runComposeUiTest {
        val fakeList = FakeSettingsListComponent(listOf("s1", "s2"))
        setContent { SettingsListContentWithEnvironment(component = fakeList) }
        onNodeWithTag("storage_item_s1").performClick()
        assertEquals("s1", fakeList.clicked)
        onNodeWithContentDescription("Back").performClick()
        assertTrue(fakeList.backInvoked)

        val fakeEditor = FakeSettingsEditorComponent()
        setContent { SettingsEditorContent(component = fakeEditor) }
        onNodeWithTag("entry_a").performTextInput("9")
        onNodeWithTag("delete_b").performClick()
        onNodeWithTag("save").performClick()
        assertTrue(fakeEditor.saveInvoked)
        assertTrue(fakeEditor.deleted.contains("b"))
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
