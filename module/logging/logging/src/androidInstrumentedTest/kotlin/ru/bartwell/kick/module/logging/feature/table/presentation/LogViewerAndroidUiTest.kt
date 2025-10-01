package ru.bartwell.kick.module.logging.feature.table.presentation

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import ru.bartwell.kick.module.logging.core.data.LogLevel
import ru.bartwell.kick.module.logging.core.persist.LogEntity

private const val TAG_LABEL_CHIPS = "label_chips"
private const val TAG_LOG_ITEM = "log_item"
private const val LABEL_A = "A"
private const val LABEL_B = "B"
private const val COUNT_ZERO = 0
private const val COUNT_ONE = 1
private const val COUNT_TWO = 2
private const val COUNT_FOUR = 4

@RunWith(AndroidJUnit4::class)
@Suppress("FunctionNaming")
class LogViewerAndroidUiTest {
    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun label_chips_and_filter_and_toggle() {
        val logs = listOf(
            LogEntity(id = 1, time = 1_000L, level = LogLevel.INFO, message = "[A][B] ab"),
            LogEntity(id = 2, time = 2_000L, level = LogLevel.ERROR, message = "[A] aaa"),
            LogEntity(id = 3, time = 3_000L, level = LogLevel.DEBUG, message = "[B] bbb"),
            LogEntity(id = 4, time = 4_000L, level = LogLevel.DEBUG, message = "zzz"),
        )
        val fake = FakeLogViewerComponent(logs)

        compose.setContent { LogViewerContent(component = fake) }

        compose.onAllNodesWithTag(TAG_LABEL_CHIPS).assertCountEquals(COUNT_ONE)

        compose.onNode(
            hasAnyAncestor(hasTestTag(TAG_LABEL_CHIPS)) and hasText(LABEL_A, substring = false)
        ).performClick()
        compose.onAllNodesWithTag(TAG_LOG_ITEM).assertCountEquals(COUNT_TWO)

        compose.onNode(
            hasAnyAncestor(hasTestTag(TAG_LABEL_CHIPS)) and hasText(LABEL_B, substring = false)
        ).performClick()
        compose.onAllNodesWithTag(TAG_LOG_ITEM).assertCountEquals(COUNT_ONE)

        compose.onNode(
            hasAnyAncestor(hasTestTag(TAG_LABEL_CHIPS)) and hasText(LABEL_A, substring = false)
        ).performClick()
        compose.onAllNodesWithTag(TAG_LOG_ITEM).assertCountEquals(COUNT_TWO)

        compose.onNode(
            hasAnyAncestor(hasTestTag(TAG_LABEL_CHIPS)) and hasText(LABEL_B, substring = false)
        ).performClick()
        compose.onAllNodesWithTag(TAG_LOG_ITEM).assertCountEquals(COUNT_FOUR)
    }

    @Test
    fun chips_hidden_when_no_labels() {
        val logs = listOf(
            LogEntity(id = 1, time = 1_000L, level = LogLevel.INFO, message = "no labels here"),
            LogEntity(id = 2, time = 2_000L, level = LogLevel.ERROR, message = "still none"),
        )
        val fake = FakeLogViewerComponent(logs)

        compose.setContent { LogViewerContent(component = fake) }

        compose.onAllNodesWithTag(TAG_LABEL_CHIPS).assertCountEquals(COUNT_ZERO)
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

        compose.setContent { LogViewerContent(component = fake) }

        compose.onNodeWithContentDescription("Filter logs").performClick()
        compose.onNode(hasAnyAncestor(androidx.compose.ui.test.isDialog()) and hasSetTextAction()).performTextInput("a")
        compose.onNodeWithText("Filter").performClick()
        compose.onAllNodesWithTag("log_item").assertCountEquals(2)

        compose.onAllNodesWithTag(TAG_LABEL_CHIPS).assertCountEquals(COUNT_ONE)

        compose.onNode(
            hasAnyAncestor(hasTestTag(TAG_LABEL_CHIPS)) and hasText(LABEL_B, substring = false)
        ).performClick()
        compose.onAllNodesWithTag(TAG_LOG_ITEM).assertCountEquals(COUNT_ONE)

        compose.onNode(
            hasAnyAncestor(hasTestTag(TAG_LABEL_CHIPS)) and hasText(LABEL_B, substring = false)
        ).performClick()
        compose.onAllNodesWithTag(TAG_LOG_ITEM).assertCountEquals(COUNT_TWO)
    }
}
