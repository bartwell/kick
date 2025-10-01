package ru.bartwell.kick.module.ktor3.feature.list.presentation

import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertCountEquals
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
import ru.bartwell.kick.module.ktor3.core.persist.RequestEntity
import ru.bartwell.kick.module.ktor3.feature.list.data.HttpMethod

private const val TAG_REQUEST_ITEM = "request_item"
private const val CD_SEARCH = "Search requests"
private const val CD_CANCEL_SEARCH = "Cancel search"
private const val CD_CLEAR_ALL = "Clear all"
private const val TEXT_OK = "OK"

@Suppress("FunctionNaming")
class RequestsListUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun new_requests_are_on_top() {
        val items = listOf(
            req(1, 1_000L, "https://example.com/old"),
            req(2, 3_000L, "https://example.com/new"),
            req(3, 2_000L, "https://example.com/mid"),
        )
        val fake = FakeRequestsListComponent(items)
        composeTestRule.setContent { RequestsListContent(component = fake) }

        val nodes = composeTestRule.onAllNodesWithTag(TAG_REQUEST_ITEM)
        nodes.assertCountEquals(3)
        nodes[0].assert(hasTextContains("new"))
        nodes[1].assert(hasTextContains("mid"))
        nodes[2].assert(hasTextContains("old"))
    }

    @Test
    fun search_apply_and_remove() {
        val items = listOf(
            req(1, 1_000L, "https://example.com/alpha"),
            req(2, 2_000L, "https://example.com/beta"),
            req(3, 3_000L, "https://example.com/alphabet"),
        )
        val fake = FakeRequestsListComponent(items)
        composeTestRule.setContent { RequestsListContent(component = fake) }

        composeTestRule.onNodeWithContentDescription(CD_SEARCH).performClick()
        composeTestRule.onNode(hasAnyAncestor(isDialog()) and hasSetTextAction()).performTextInput("alpha")
        composeTestRule.onNodeWithText(TEXT_OK).performClick()

        composeTestRule.onAllNodesWithTag(TAG_REQUEST_ITEM).assertCountEquals(2)

        composeTestRule.onNodeWithContentDescription(CD_CANCEL_SEARCH).performClick()
        composeTestRule.onAllNodesWithTag(TAG_REQUEST_ITEM).assertCountEquals(3)
    }

    @Test
    fun clear_all() {
        val items = listOf(
            req(1, 1_000L, "https://example.com/a"),
            req(2, 2_000L, "https://example.com/b"),
        )
        val fake = FakeRequestsListComponent(items)
        composeTestRule.setContent { RequestsListContent(component = fake) }

        composeTestRule.onAllNodesWithTag(TAG_REQUEST_ITEM).assertCountEquals(2)
        composeTestRule.onNodeWithContentDescription(CD_CLEAR_ALL).performClick()
        composeTestRule.onAllNodesWithTag(TAG_REQUEST_ITEM).assertCountEquals(0)
    }
}

private fun req(id: Long, ts: Long, url: String) = RequestEntity(
    id = id,
    timestamp = ts,
    method = HttpMethod.GET,
    url = url,
    statusCode = 200,
    durationMs = 10,
    responseSizeBytes = 100,
    isSecure = true,
    error = null,
    requestHeaders = null,
    requestBody = null,
    responseHeaders = null,
    responseBody = null,
)

private fun hasTextContains(sub: String) = androidx.compose.ui.test.hasText(substring = true, text = sub)
