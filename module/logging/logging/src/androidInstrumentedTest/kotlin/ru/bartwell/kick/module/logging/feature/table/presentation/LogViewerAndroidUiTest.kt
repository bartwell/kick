package ru.bartwell.kick.module.logging.feature.table.presentation

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.isDialog
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import ru.bartwell.kick.module.logging.core.data.LogLevel
import ru.bartwell.kick.module.logging.core.persist.LogEntity

private const val TAG_LOG_LIST = "log_list"
private const val TAG_LOG_ITEM = "log_item"
private const val CD_FILTER = "Filter logs"
private const val CD_DISABLE_FILTER = "Disable filter"
private const val CD_CLEAR_ALL = "Clear all"
private const val CD_SHARE_LOGS = "Share logs"
private const val TEXT_FILTER = "Filter"
private const val COUNT_ZERO = 0
private const val COUNT_TWO = 2
private const val COUNT_THREE = 3

@Suppress("FunctionNaming")
@RunWith(AndroidJUnit4::class)
class LogViewerAndroidUiTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun new_messages_are_on_top() {
        val logs = listOf(
            LogEntity(id = 1, time = 1_000L, level = LogLevel.INFO, message = "old"),
            LogEntity(id = 2, time = 3_000L, level = LogLevel.ERROR, message = "new"),
            LogEntity(id = 3, time = 2_000L, level = LogLevel.DEBUG, message = "mid"),
        )
        val fake = FakeLogViewerComponent(logs)

        composeTestRule.setContent { LogViewerContent(component = fake) }

        composeTestRule.onAllNodesWithTag(TAG_LOG_LIST).assertCountEquals(1)
        val items = composeTestRule.onAllNodesWithTag(TAG_LOG_ITEM)
        items.assertCountEquals(COUNT_THREE)
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

        composeTestRule.onNodeWithContentDescription(CD_FILTER).performClick()
        composeTestRule
            .onNode(hasAnyAncestor(isDialog()) and hasSetTextAction())
            .performTextInput("alpha")
        composeTestRule.onNodeWithText(TEXT_FILTER).performClick()

        composeTestRule.onNodeWithContentDescription(CD_DISABLE_FILTER).assertIsDisplayed()
        composeTestRule.onAllNodesWithTag(TAG_LOG_ITEM).assertCountEquals(COUNT_TWO)

        composeTestRule.onNodeWithContentDescription(CD_DISABLE_FILTER).performClick()
        composeTestRule.onAllNodesWithTag(TAG_LOG_ITEM).assertCountEquals(COUNT_THREE)
    }

    @Test
    fun clear_log() {
        val logs = listOf(
            LogEntity(id = 1, time = 1_000L, level = LogLevel.INFO, message = "m1"),
            LogEntity(id = 2, time = 2_000L, level = LogLevel.ERROR, message = "m2"),
        )
        val fake = FakeLogViewerComponent(logs)

        composeTestRule.setContent { LogViewerContent(component = fake) }

        composeTestRule.onAllNodesWithTag(TAG_LOG_ITEM).assertCountEquals(COUNT_TWO)
        composeTestRule.onNodeWithContentDescription(CD_CLEAR_ALL).performClick()
        composeTestRule.onAllNodesWithTag(TAG_LOG_ITEM).assertCountEquals(COUNT_ZERO)
    }

    @Test
    fun share_or_copy_action_invoked() {
        val logs = listOf(
            LogEntity(id = 1, time = 1_000L, level = LogLevel.INFO, message = "m1"),
        )
        val fake = FakeLogViewerComponent(logs)

        composeTestRule.setContent { LogViewerContent(component = fake) }

        composeTestRule.onNodeWithContentDescription(CD_SHARE_LOGS).performClick()
        assertTrue(fake.shareInvoked)
    }
}

private fun hasTextContains(sub: String) = androidx.compose.ui.test.hasText(substring = true, text = sub)
