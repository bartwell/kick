package ru.bartwell.kick.module.logging.feature.table.presentation

import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.isDialog
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import org.junit.Rule
import org.junit.Test
import ru.bartwell.kick.module.logging.core.data.LogLevel
import ru.bartwell.kick.module.logging.core.persist.LogEntity
import kotlin.test.assertTrue

@Suppress("FunctionNaming")
class LogViewerUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun new_messages_are_on_top() {
        val logs = listOf(
            LogEntity(id = 1, time = 1_000L, level = LogLevel.INFO, message = "old"),
            LogEntity(id = 2, time = 3_000L, level = LogLevel.ERROR, message = "new"),
            LogEntity(id = 3, time = 2_000L, level = LogLevel.DEBUG, message = "mid"),
        )
        val fake = FakeLogViewerComponent(logs)

        composeTestRule.setContent {
            LogViewerContent(component = fake)
        }

        // Expect sorted by time DESC: new, mid, old
        composeTestRule.onAllNodesWithTag("log_list").assertCountEquals(1)
        val items = composeTestRule.onAllNodesWithTag("log_item")
        items.assertCountEquals(3)
        items[0].assertIsDisplayed()
        items[0].assert(hasTextContains("new"))
        items[1].assert(hasTextContains("mid"))
        items[2].assert(hasTextContains("old"))
    }

    @Test
    fun filter_apply_and_remove() {
        val logs = listOf(
            LogEntity(id = 1, time = 1_000L, level = LogLevel.INFO, message = "alpha"),
            LogEntity(id = 2, time = 2_000L, level = LogLevel.ERROR, message = "beta"),
            LogEntity(id = 3, time = 3_000L, level = LogLevel.DEBUG, message = "alphabet"),
        )
        val fake = FakeLogViewerComponent(logs)

        composeTestRule.setContent { LogViewerContent(component = fake) }

        // Open filter dialog
        composeTestRule.onNodeWithContentDescription("Filter logs").performClick()

        // Enter query 'alpha' and apply
        composeTestRule
            .onNode(hasAnyAncestor(isDialog()) and hasSetTextAction())
            .performTextInput("alpha")
        composeTestRule.onNodeWithText("Filter").performClick()

        // Now filter is active, icon changed and only 2 items match (alpha, alphabet)
        composeTestRule.onNodeWithContentDescription("Disable filter").assertIsDisplayed()
        composeTestRule.onAllNodesWithTag("log_item").assertCountEquals(2)

        // Disable filter
        composeTestRule.onNodeWithContentDescription("Disable filter").performClick()

        // All items visible again
        composeTestRule.onAllNodesWithTag("log_item").assertCountEquals(3)
    }

    @Test
    fun clear_log() {
        val logs = listOf(
            LogEntity(id = 1, time = 1_000L, level = LogLevel.INFO, message = "m1"),
            LogEntity(id = 2, time = 2_000L, level = LogLevel.ERROR, message = "m2"),
        )
        val fake = FakeLogViewerComponent(logs)

        composeTestRule.setContent { LogViewerContent(component = fake) }

        composeTestRule.onAllNodesWithTag("log_item").assertCountEquals(2)
        composeTestRule.onNodeWithContentDescription("Clear all").performClick()
        composeTestRule.onAllNodesWithTag("log_item").assertCountEquals(0)
    }

    @Test
    fun share_or_copy_action_invoked() {
        val logs = listOf(
            LogEntity(id = 1, time = 1_000L, level = LogLevel.INFO, message = "m1"),
        )
        val fake = FakeLogViewerComponent(logs)

        composeTestRule.setContent { LogViewerContent(component = fake) }

        // On JVM platform the description is "Share logs"
        composeTestRule.onNodeWithContentDescription("Share logs").performClick()
        assertTrue(fake.shareInvoked)
    }
}

// Helpers
private fun hasTextContains(sub: String) = androidx.compose.ui.test.hasText(substring = true, text = sub)
