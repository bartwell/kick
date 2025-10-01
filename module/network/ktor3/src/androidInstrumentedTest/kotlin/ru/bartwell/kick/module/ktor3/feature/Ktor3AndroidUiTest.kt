package ru.bartwell.kick.module.ktor3.feature

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertCountEquals
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
import ru.bartwell.kick.module.ktor3.core.persist.RequestEntity
import ru.bartwell.kick.module.ktor3.feature.detail.presentation.FakeRequestDetailsComponent
import ru.bartwell.kick.module.ktor3.feature.detail.presentation.RequestDetailsContent
import ru.bartwell.kick.module.ktor3.feature.list.data.HttpMethod
import ru.bartwell.kick.module.ktor3.feature.list.presentation.FakeRequestsListComponent
import ru.bartwell.kick.module.ktor3.feature.list.presentation.RequestsListContent

private const val TAG_REQUEST_ITEM = "request_item"
private const val CD_SEARCH = "Search requests"
private const val CD_CANCEL_SEARCH = "Cancel search"
private const val CD_CLEAR_ALL = "Clear all"
private const val TEXT_OK = "OK"
private const val ID1 = 1L
private const val ID2 = 2L
private const val ID3 = 3L
private const val TS1 = 1_000L
private const val TS2 = 2_000L
private const val TS3 = 3_000L
private const val COUNT_ONE = 1
private const val COUNT_THREE = 3

@Suppress("FunctionNaming")
@RunWith(AndroidJUnit4::class)
class Ktor3AndroidUiTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun list_new_requests_top_and_search_and_clear() {
        val items = listOf(
            req(ID1, TS1, "https://example.com/old"),
            req(ID2, TS3, "https://example.com/new"),
            req(ID3, TS2, "https://example.com/mid"),
        )
        val fake = FakeRequestsListComponent(items)
        composeTestRule.setContent { RequestsListContent(component = fake) }

        val nodes = composeTestRule.onAllNodesWithTag(TAG_REQUEST_ITEM)
        nodes.assertCountEquals(COUNT_THREE)
        nodes[0].assert(hasTextContains("new"))
        nodes[1].assert(hasTextContains("mid"))
        nodes[2].assert(hasTextContains("old"))

        composeTestRule.onNodeWithContentDescription(CD_SEARCH).performClick()
        composeTestRule.onNode(hasAnyAncestor(isDialog()) and hasSetTextAction()).performTextInput("new")
        composeTestRule.onNodeWithText(TEXT_OK).performClick()
        composeTestRule.onAllNodesWithTag(TAG_REQUEST_ITEM).assertCountEquals(COUNT_ONE)

        composeTestRule.onNodeWithContentDescription(CD_CANCEL_SEARCH).performClick()
        composeTestRule.onAllNodesWithTag(TAG_REQUEST_ITEM).assertCountEquals(COUNT_THREE)

        composeTestRule.onNodeWithContentDescription(CD_CLEAR_ALL).performClick()
        composeTestRule.onAllNodesWithTag(TAG_REQUEST_ITEM).assertCountEquals(0)
    }

    @Test
    fun details_copy_invoked() {
        val request = req(ID1, TS1, "https://example.com/a")
        val fake = FakeRequestDetailsComponent(request)
        composeTestRule.setContent { RequestDetailsContent(component = fake) }

        composeTestRule.onNodeWithContentDescription("Copy all details").performClick()
        assertTrue(fake.copyInvoked)
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
