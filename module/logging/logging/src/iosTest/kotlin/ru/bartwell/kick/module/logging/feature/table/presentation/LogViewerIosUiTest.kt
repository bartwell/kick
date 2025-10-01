package ru.bartwell.kick.module.logging.feature.table.presentation

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isDialog
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.runComposeUiTest
import ru.bartwell.kick.module.logging.core.data.LogLevel
import ru.bartwell.kick.module.logging.core.persist.LogEntity
import kotlin.test.Test
import kotlin.test.assertTrue

@Suppress("FunctionNaming")
@OptIn(ExperimentalTestApi::class)
class LogViewerIosUiTest {

    @Test
    fun filter_and_clear_and_share() = runComposeUiTest {
        val logs = listOf(
            LogEntity(id = 1, time = 1_000L, level = LogLevel.INFO, message = "alpha"),
            LogEntity(id = 2, time = 2_000L, level = LogLevel.ERROR, message = "beta"),
            LogEntity(id = 3, time = 3_000L, level = LogLevel.DEBUG, message = "alphabet"),
        )
        val fake = FakeLogViewerComponent(logs)

        setContent { LogViewerContent(component = fake) }

        // Filter
        onNodeWithContentDescription("Filter logs").performClick()
        onNode(hasAnyAncestor(isDialog()) and hasSetTextAction()).performTextInput("alpha")
        onNodeWithText("Filter").performClick()
        onAllNodesWithTag("log_item").assertCountEquals(2)

        // Disable filter
        onNodeWithContentDescription("Disable filter").performClick()
        onAllNodesWithTag("log_item").assertCountEquals(3)

        // Clear
        onNodeWithContentDescription("Clear all").performClick()
        onAllNodesWithTag("log_item").assertCountEquals(0)

        // Copy on iOS
        onNodeWithContentDescription("Copy logs").performClick()
        assertTrue(fake.shareInvoked)
    }

    @Test
    fun label_chips_and_filter_and_toggle() = runComposeUiTest {
        val logs = listOf(
            LogEntity(id = 1, time = 1_000L, level = LogLevel.INFO, message = "[A][B] ab"),
            LogEntity(id = 2, time = 2_000L, level = LogLevel.ERROR, message = "[A] aaa"),
            LogEntity(id = 3, time = 3_000L, level = LogLevel.DEBUG, message = "[B] bbb"),
            LogEntity(id = 4, time = 4_000L, level = LogLevel.DEBUG, message = "zzz"),
        )
        val fake = FakeLogViewerComponent(logs)

        setContent { LogViewerContent(component = fake) }

        onAllNodesWithTag("label_chips").assertCountEquals(1)

        onNode(hasAnyAncestor(hasTestTag("label_chips")) and hasText("A", substring = false)).performClick()
        onAllNodesWithTag("log_item").assertCountEquals(2)

        onNode(hasAnyAncestor(hasTestTag("label_chips")) and hasText("B", substring = false)).performClick()
        onAllNodesWithTag("log_item").assertCountEquals(1)

        onNode(hasAnyAncestor(hasTestTag("label_chips")) and hasText("A", substring = false)).performClick()
        onAllNodesWithTag("log_item").assertCountEquals(2)

        onNode(hasAnyAncestor(hasTestTag("label_chips")) and hasText("B", substring = false)).performClick()
        onAllNodesWithTag("log_item").assertCountEquals(4)
    }

    @Test
    fun chips_hidden_when_no_labels() = runComposeUiTest {
        val logs = listOf(
            LogEntity(id = 1, time = 1_000L, level = LogLevel.INFO, message = "no labels here"),
            LogEntity(id = 2, time = 2_000L, level = LogLevel.ERROR, message = "still none"),
        )
        val fake = FakeLogViewerComponent(logs)

        setContent { LogViewerContent(component = fake) }

        onAllNodesWithTag("label_chips").assertCountEquals(0)
    }

    @Test
    fun combine_text_filter_and_label_filter() = runComposeUiTest {
        val logs = listOf(
            LogEntity(id = 1, time = 1_000L, level = LogLevel.INFO, message = "[A][B] abcd"),
            LogEntity(id = 2, time = 2_000L, level = LogLevel.ERROR, message = "[A] aaa"),
            LogEntity(id = 3, time = 3_000L, level = LogLevel.DEBUG, message = "[B] bbb"),
            LogEntity(id = 4, time = 4_000L, level = LogLevel.DEBUG, message = "zzz"),
        )
        val fake = FakeLogViewerComponent(logs)

        setContent { LogViewerContent(component = fake) }

        onNodeWithContentDescription("Filter logs").performClick()
        onNode(hasAnyAncestor(isDialog()) and hasSetTextAction()).performTextInput("a")
        onNodeWithText("Filter").performClick()
        onAllNodesWithTag("log_item").assertCountEquals(2)

        onAllNodesWithTag("label_chips").assertCountEquals(1)

        onNode(hasAnyAncestor(hasTestTag("label_chips")) and hasText("B", substring = false)).performClick()
        onAllNodesWithTag("log_item").assertCountEquals(1)

        onNode(hasAnyAncestor(hasTestTag("label_chips")) and hasText("B", substring = false)).performClick()
        onAllNodesWithTag("log_item").assertCountEquals(2)
    }
}
