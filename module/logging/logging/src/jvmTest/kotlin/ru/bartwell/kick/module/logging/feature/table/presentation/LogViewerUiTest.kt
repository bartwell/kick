package ru.bartwell.kick.module.logging.feature.table.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isDialog
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import org.junit.Rule
import org.junit.Test
import ru.bartwell.kick.core.presentation.AppUiEnvironment
import ru.bartwell.kick.core.presentation.LocalAppUiEnvironment
import ru.bartwell.kick.module.logging.core.data.LogLevel
import ru.bartwell.kick.module.logging.core.persist.LogEntity
import kotlin.test.assertTrue

@Suppress("FunctionNaming")
class LogViewerUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun new_messages_are_at_bottom() {
        val logs = listOf(
            LogEntity(id = 1, time = 1_000L, level = LogLevel.INFO, message = "old"),
            LogEntity(id = 2, time = 3_000L, level = LogLevel.ERROR, message = "new"),
            LogEntity(id = 3, time = 2_000L, level = LogLevel.DEBUG, message = "mid"),
        )
        val fake = FakeLogViewerComponent(logs)

        composeTestRule.setContent {
            LogViewerContentWithEnvironment(fake)
        }

        // Expect sorted by time ASC: old, mid, new
        composeTestRule.onAllNodesWithTag("log_list").assertCountEquals(1)
        val items = composeTestRule.onAllNodesWithTag("log_item")
        items.assertCountEquals(3)
        items[0].assertIsDisplayed()
        items[0].assert(hasTextContains("old"))
        items[1].assert(hasTextContains("mid"))
        items[2].assert(hasTextContains("new"))
    }

    @Test
    fun auto_scroll_toggle_is_enabled_by_default() {
        val logs = listOf(
            LogEntity(id = 1, time = 1_000L, level = LogLevel.INFO, message = "m1"),
        )
        val fake = FakeLogViewerComponent(logs)

        composeTestRule.setContent { LogViewerContentWithEnvironment(fake) }

        composeTestRule.onNodeWithContentDescription("Disable auto-scroll").performClick()
        composeTestRule.onNodeWithContentDescription("Enable auto-scroll").assertIsDisplayed()
    }

    @Test
    fun filter_apply_and_remove() {
        val logs = listOf(
            LogEntity(id = 1, time = 1_000L, level = LogLevel.INFO, message = "alpha"),
            LogEntity(id = 2, time = 2_000L, level = LogLevel.ERROR, message = "beta"),
            LogEntity(id = 3, time = 3_000L, level = LogLevel.DEBUG, message = "alphabet"),
        )
        val fake = FakeLogViewerComponent(logs)

        composeTestRule.setContent { LogViewerContentWithEnvironment(fake) }

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

        composeTestRule.setContent { LogViewerContentWithEnvironment(fake) }

        composeTestRule.onAllNodesWithTag("log_item").assertCountEquals(2)
        composeTestRule.onNodeWithContentDescription("Clear all").performClick()
        composeTestRule.onAllNodesWithTag("log_item").assertCountEquals(0)
    }

    @Test
    fun copy_action_invoked() {
        val logs = listOf(
            LogEntity(id = 1, time = 1_000L, level = LogLevel.INFO, message = "m1"),
        )
        val fake = FakeLogViewerComponent(logs)

        composeTestRule.setContent { LogViewerContentWithEnvironment(fake) }

        composeTestRule.onNodeWithContentDescription("Menu").performClick()
        composeTestRule.onNodeWithText("Copy").performClick()
        assertTrue(fake.copyInvoked)
    }

    @Test
    fun save_to_file_action_invoked() {
        val logs = listOf(
            LogEntity(id = 1, time = 1_000L, level = LogLevel.INFO, message = "m1"),
        )
        val fake = FakeLogViewerComponent(logs)

        composeTestRule.setContent { LogViewerContentWithEnvironment(fake) }

        composeTestRule.onNodeWithContentDescription("Menu").performClick()
        composeTestRule.onNodeWithText("Save to file").performClick()
        assertTrue(fake.saveInvoked)

        composeTestRule.onNodeWithContentDescription("Menu").performClick()
        composeTestRule.onNodeWithText("Share as text").assertDoesNotExist()
        composeTestRule.onNodeWithText("Share as file").assertDoesNotExist()
    }

    @Test
    fun label_chips_and_filter_and_toggle() {
        val logs = listOf(
            LogEntity(id = 1, time = 1_000L, level = LogLevel.INFO, message = "[A][B] ab"),
            LogEntity(id = 2, time = 2_000L, level = LogLevel.ERROR, message = "[A] aaa"),
            LogEntity(id = 3, time = 3_000L, level = LogLevel.DEBUG, message = "[B] bbb"),
            LogEntity(id = 4, time = 4_000L, level = LogLevel.DEBUG, message = "zzz"),
        )
        val fake = FakeLogViewerComponent(logs)

        composeTestRule.setContent { LogViewerContentWithEnvironment(fake) }

        // Chips visible with two labels
        composeTestRule.onAllNodesWithTag("label_chips").assertCountEquals(1)

        // Select A -> two items: [A][B] ab, [A] aaa
        composeTestRule.onNode(
            hasAnyAncestor(hasTestTag("label_chips")) and hasText("A", substring = false)
        ).performClick()
        composeTestRule.onAllNodesWithTag("log_item").assertCountEquals(2)

        // Add B -> AND filter -> only [A][B] ab
        composeTestRule.onNode(
            hasAnyAncestor(hasTestTag("label_chips")) and hasText("B", substring = false)
        ).performClick()
        composeTestRule.waitUntil {
            fake.model.value.selectedLabels.contains("B")
        }
        composeTestRule.onAllNodesWithTag("log_item").assertCountEquals(1)

        // Deselect A -> only B selected -> two items: [A][B] ab, [B] bbb
        composeTestRule.onNode(
            hasAnyAncestor(hasTestTag("label_chips")) and hasText("A", substring = false)
        ).performClick()
        composeTestRule.onAllNodesWithTag("log_item").assertCountEquals(2)

        // Deselect B -> show all
        composeTestRule.onNode(
            hasAnyAncestor(hasTestTag("label_chips")) and hasText("B", substring = false)
        ).performClick()
        composeTestRule.onAllNodesWithTag("log_item").assertCountEquals(4)
    }

    @Test
    fun chips_hidden_when_no_labels() {
        val logs = listOf(
            LogEntity(id = 1, time = 1_000L, level = LogLevel.INFO, message = "no labels here"),
            LogEntity(id = 2, time = 2_000L, level = LogLevel.ERROR, message = "still none"),
        )
        val fake = FakeLogViewerComponent(logs)

        composeTestRule.setContent { LogViewerContentWithEnvironment(fake) }

        composeTestRule.onAllNodesWithTag("label_chips").assertCountEquals(0)
    }

    @Test
    fun combine_text_filter_and_label_filter() {
        val logs = listOf(
            LogEntity(id = 1, time = 1_000L, level = LogLevel.INFO, message = "[A][B] abcd"),
            LogEntity(id = 2, time = 2_000L, level = LogLevel.ERROR, message = "[A] aaa"),
            LogEntity(id = 3, time = 3_000L, level = LogLevel.DEBUG, message = "[B] bbb"),
            LogEntity(id = 4, time = 4_000L, level = LogLevel.DEBUG, message = "zzz"),
        )
        val fake = FakeLogViewerComponent(logs)

        composeTestRule.setContent { LogViewerContentWithEnvironment(fake) }

        // Apply text filter: 'a' -> should leave 2 items (abcd, aaa)
        composeTestRule.onNodeWithContentDescription("Filter logs").performClick()
        composeTestRule.onNode(hasAnyAncestor(isDialog()) and hasSetTextAction()).performTextInput("a")
        composeTestRule.onNodeWithText("Filter").performClick()
        composeTestRule.onAllNodesWithTag("log_item").assertCountEquals(2)

        // Chips visible and include B (present in abcd)
        composeTestRule.onAllNodesWithTag("label_chips").assertCountEquals(1)

        // Select B -> AND with text filter => only [A][B] abcd remains
        composeTestRule.onNode(
            hasAnyAncestor(hasTestTag("label_chips")) and hasText("B", substring = false)
        ).performClick()
        composeTestRule.onAllNodesWithTag("log_item").assertCountEquals(1)

        // Deselect B -> back to 2
        composeTestRule.onNode(
            hasAnyAncestor(hasTestTag("label_chips")) and hasText("B", substring = false)
        ).performClick()
        composeTestRule.waitUntil {
            !fake.model.value.selectedLabels.contains("B")
        }
        composeTestRule.onAllNodesWithTag("log_item").assertCountEquals(2)
    }
}

@Composable
private fun LogViewerContentWithEnvironment(component: FakeLogViewerComponent) {
    CompositionLocalProvider(
        LocalAppUiEnvironment provides AppUiEnvironment(
            screenCloser = {},
            canNavigateBack = true,
        )
    ) {
        LogViewerContent(component = component)
    }
}

// Helpers
private fun hasTextContains(sub: String) = androidx.compose.ui.test.hasText(substring = true, text = sub)
