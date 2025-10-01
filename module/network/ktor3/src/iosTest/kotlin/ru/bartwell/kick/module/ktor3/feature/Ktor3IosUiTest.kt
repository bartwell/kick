package ru.bartwell.kick.module.ktor3.feature

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
import ru.bartwell.kick.module.ktor3.core.persist.RequestEntity
import ru.bartwell.kick.module.ktor3.feature.detail.presentation.FakeRequestDetailsComponent
import ru.bartwell.kick.module.ktor3.feature.detail.presentation.RequestDetailsContent
import ru.bartwell.kick.module.ktor3.feature.list.data.HttpMethod
import ru.bartwell.kick.module.ktor3.feature.list.presentation.FakeRequestsListComponent
import ru.bartwell.kick.module.ktor3.feature.list.presentation.RequestsListContent
import kotlin.test.Test
import kotlin.test.assertTrue

@Suppress("FunctionNaming")
@OptIn(ExperimentalTestApi::class)
class Ktor3IosUiTest {

    @Test
    fun list_and_details() = runComposeUiTest {
        val items = listOf(
            req(1, 1_000L, "https://example.com/alpha"),
            req(2, 2_000L, "https://example.com/beta"),
            req(3, 3_000L, "https://example.com/alphabet"),
        )
        val fakeList = FakeRequestsListComponent(items)
        setContent { RequestsListContent(component = fakeList) }

        onAllNodesWithTag("request_item").assertCountEquals(3)
        onNodeWithContentDescription("Search requests").performClick()
        onNode(hasAnyAncestor(isDialog()) and hasSetTextAction()).performTextInput("alpha")
        onNodeWithText("OK").performClick()
        onAllNodesWithTag("request_item").assertCountEquals(2)
        onNodeWithContentDescription("Cancel search").performClick()
        onAllNodesWithTag("request_item").assertCountEquals(3)

        val fakeDetails = FakeRequestDetailsComponent(req(10, 4_000L, "https://example.com/x"))
        setContent { RequestDetailsContent(component = fakeDetails) }
        onNodeWithContentDescription("Copy all details").performClick()
        assertTrue(fakeDetails.copyInvoked)
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
