package ru.bartwell.kick.module.sqlite.runtime.feature

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.runComposeUiTest
import ru.bartwell.kick.module.sqlite.runtime.feature.update.presentation.FakeUpdateComponent
import ru.bartwell.kick.module.sqlite.runtime.feature.update.presentation.UpdateContent
import kotlin.test.Test
import kotlin.test.assertTrue

@Suppress("FunctionNaming")
@OptIn(ExperimentalTestApi::class)
class SqliteRuntimeWasmUiTest {
    @Test
    fun update_only() = runComposeUiTest {
        val fakeUpdate = FakeUpdateComponent()
        setContent { UpdateContent(component = fakeUpdate) }
        onNodeWithTag("update_value").performTextInput("XYZ")
        onNodeWithTag("null_checkbox").performClick()
        onNodeWithText("Save").performClick()
        assertTrue(fakeUpdate.saveInvoked)
    }
}
