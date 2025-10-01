package ru.bartwell.kick.module.logging.feature.table.presentation

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasSetTextAction
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
}
