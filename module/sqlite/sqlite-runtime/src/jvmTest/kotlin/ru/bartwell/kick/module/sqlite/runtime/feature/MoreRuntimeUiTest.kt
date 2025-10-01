package ru.bartwell.kick.module.sqlite.runtime.feature

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import org.junit.Rule
import org.junit.Test
import ru.bartwell.kick.module.sqlite.runtime.feature.insert.presentation.FakeInsertComponent
import ru.bartwell.kick.module.sqlite.runtime.feature.insert.presentation.InsertContent
import ru.bartwell.kick.module.sqlite.runtime.feature.query.presentation.FakeQueryComponent
import ru.bartwell.kick.module.sqlite.runtime.feature.query.presentation.QueryContent
import ru.bartwell.kick.module.sqlite.runtime.feature.structure.presentation.FakeStructureComponent
import ru.bartwell.kick.module.sqlite.runtime.feature.structure.presentation.StructureContent
import kotlin.test.assertTrue

@Suppress("FunctionNaming")
class MoreRuntimeUiTest {
    @get:Rule val compose = createComposeRule()

    @Test
    fun insert_adds_row() {
        val fake = FakeInsertComponent()
        compose.setContent { InsertContent(component = fake) }
        compose.onNodeWithTag("insert_input_text").performTextInput("Hello")
        compose.onNodeWithTag("insert_btn").performClick()
        assertTrue(fake.saveInvoked)
    }

    @Test
    fun query_execute_and_message() {
        val fake = FakeQueryComponent()
        compose.setContent { QueryContent(component = fake) }
        // success path: shows table (no message), but we can still click
        compose.onNodeWithTag("query_input").performTextInput("select * from items")
        compose.onNodeWithTag("execute_btn").performClick()
        // error path: shows message
        compose.onNodeWithTag("query_input").performTextInput(" error ")
        compose.onNodeWithTag("execute_btn").performClick()
        compose.onNodeWithTag("query_message").assertExists()
    }

    @Test
    fun structure_shows_columns() {
        val fake = FakeStructureComponent()
        compose.setContent { StructureContent(component = fake) }
        compose.onNodeWithText("Name: _id").assertExists()
        compose.onNodeWithText("Type: INTEGER").assertExists()
        compose.onNodeWithText("Name: text").assertExists()
        compose.onNodeWithText("Type: TEXT").assertExists()
    }
}
