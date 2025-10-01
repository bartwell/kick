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

private const val TAG_LOG_ITEM = "log_item"
private const val TAG_LABEL_CHIPS = "label_chips"
private const val LABEL_A = "A"
private const val LABEL_B = "B"
private const val CD_FILTER = "Filter logs"
private const val CD_DISABLE_FILTER = "Disable filter"
private const val CD_CLEAR_ALL = "Clear all"
private const val CD_COPY_LOGS = "Copy logs"
private const val TEXT_FILTER = "Filter"
private const val COUNT_ZERO = 0
private const val COUNT_TWO = 2
private const val COUNT_THREE = 3
private const val COUNT_ONE = 1
private const val COUNT_FOUR = 4

@Suppress("FunctionNaming")
@OptIn(ExperimentalTestApi::class)
class LogViewerWasmUiTest {

    @Test
    fun wasm_filter_clear_share() = runComposeUiTest {
        val logs = listOf(
            LogEntity(id = 1, time = 1_000L, level = LogLevel.INFO, message = "alpha"),
            LogEntity(id = 2, time = 2_000L, level = LogLevel.ERROR, message = "beta"),
            LogEntity(id = 3, time = 3_000L, level = LogLevel.DEBUG, message = "alphabet"),
        )
        val fake = FakeLogViewerComponent(logs)

        setContent { LogViewerContent(component = fake) }

        onNodeWithContentDescription(CD_FILTER).performClick()
        onNode(hasAnyAncestor(isDialog()) and hasSetTextAction()).performTextInput("alpha")
        onNodeWithText(TEXT_FILTER).performClick()
        onAllNodesWithTag(TAG_LOG_ITEM).assertCountEquals(COUNT_TWO)

        onNodeWithContentDescription(CD_DISABLE_FILTER).performClick()
        onAllNodesWithTag(TAG_LOG_ITEM).assertCountEquals(COUNT_THREE)

        onNodeWithContentDescription(CD_CLEAR_ALL).performClick()
        onAllNodesWithTag(TAG_LOG_ITEM).assertCountEquals(COUNT_ZERO)

        // On Web platform compose.uiTest uses same contentDescription
        onNodeWithContentDescription(CD_COPY_LOGS).performClick()
        assertTrue(fake.shareInvoked)
    }

    @Test
    fun wasm_label_chips_and_filter_and_toggle() = runComposeUiTest {
        val logs = listOf(
            LogEntity(id = 1, time = 1_000L, level = LogLevel.INFO, message = "[A][B] ab"),
            LogEntity(id = 2, time = 2_000L, level = LogLevel.ERROR, message = "[A] aaa"),
            LogEntity(id = 3, time = 3_000L, level = LogLevel.DEBUG, message = "[B] bbb"),
            LogEntity(id = 4, time = 4_000L, level = LogLevel.DEBUG, message = "zzz"),
        )
        val fake = FakeLogViewerComponent(logs)

        setContent { LogViewerContent(component = fake) }

        onAllNodesWithTag(TAG_LABEL_CHIPS).assertCountEquals(COUNT_ONE)

        onNode(hasAnyAncestor(hasTestTag(TAG_LABEL_CHIPS)) and hasText(LABEL_A, substring = false)).performClick()
        onAllNodesWithTag(TAG_LOG_ITEM).assertCountEquals(COUNT_TWO)

        onNode(hasAnyAncestor(hasTestTag(TAG_LABEL_CHIPS)) and hasText(LABEL_B, substring = false)).performClick()
        onAllNodesWithTag(TAG_LOG_ITEM).assertCountEquals(COUNT_ONE)

        onNode(hasAnyAncestor(hasTestTag(TAG_LABEL_CHIPS)) and hasText(LABEL_A, substring = false)).performClick()
        onAllNodesWithTag(TAG_LOG_ITEM).assertCountEquals(COUNT_TWO)

        onNode(hasAnyAncestor(hasTestTag(TAG_LABEL_CHIPS)) and hasText(LABEL_B, substring = false)).performClick()
        onAllNodesWithTag(TAG_LOG_ITEM).assertCountEquals(COUNT_FOUR)
    }

    @Test
    fun wasm_chips_hidden_when_no_labels() = runComposeUiTest {
        val logs = listOf(
            LogEntity(id = 1, time = 1_000L, level = LogLevel.INFO, message = "no labels here"),
            LogEntity(id = 2, time = 2_000L, level = LogLevel.ERROR, message = "still none"),
        )
        val fake = FakeLogViewerComponent(logs)

        setContent { LogViewerContent(component = fake) }

        onAllNodesWithTag(TAG_LABEL_CHIPS).assertCountEquals(COUNT_ZERO)
    }

    @Test
    fun wasm_combine_text_filter_and_label_filter() = runComposeUiTest {
        val logs = listOf(
            LogEntity(id = 1, time = 1_000L, level = LogLevel.INFO, message = "[A][B] abcd"),
            LogEntity(id = 2, time = 2_000L, level = LogLevel.ERROR, message = "[A] aaa"),
            LogEntity(id = 3, time = 3_000L, level = LogLevel.DEBUG, message = "[B] bbb"),
            LogEntity(id = 4, time = 4_000L, level = LogLevel.DEBUG, message = "zzz"),
        )
        val fake = FakeLogViewerComponent(logs)

        setContent { LogViewerContent(component = fake) }

        onNodeWithContentDescription(CD_FILTER).performClick()
        onNode(hasAnyAncestor(isDialog()) and hasSetTextAction()).performTextInput("a")
        onNodeWithText(TEXT_FILTER).performClick()
        onAllNodesWithTag(TAG_LOG_ITEM).assertCountEquals(COUNT_TWO)

        onAllNodesWithTag(TAG_LABEL_CHIPS).assertCountEquals(COUNT_ONE)

        onNode(hasAnyAncestor(hasTestTag(TAG_LABEL_CHIPS)) and hasText(LABEL_B, substring = false)).performClick()
        onAllNodesWithTag(TAG_LOG_ITEM).assertCountEquals(COUNT_ONE)

        onNode(hasAnyAncestor(hasTestTag(TAG_LABEL_CHIPS)) and hasText(LABEL_B, substring = false)).performClick()
        onAllNodesWithTag(TAG_LOG_ITEM).assertCountEquals(COUNT_TWO)
    }
}
