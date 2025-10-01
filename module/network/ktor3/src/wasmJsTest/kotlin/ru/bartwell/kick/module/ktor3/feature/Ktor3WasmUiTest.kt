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

private const val TAG_REQUEST_ITEM = "request_item"
private const val CD_SEARCH = "Search requests"
private const val CD_CANCEL_SEARCH = "Cancel search"
private const val TEXT_OK = "OK"
private const val CD_COPY_ALL = "Copy all details"
private const val ID1 = 1L
private const val ID2 = 2L
private const val ID3 = 3L
private const val ID10 = 10L
private const val TS1 = 1_000L
private const val TS2 = 2_000L
private const val TS3 = 3_000L
private const val TS4 = 4_000L
private const val COUNT_TWO = 2
private const val COUNT_THREE = 3

@Suppress("FunctionNaming")
@OptIn(ExperimentalTestApi::class)
class Ktor3WasmUiTest {

    @Test
    fun list_and_details() = runComposeUiTest {
        val items = listOf(
            req(ID1, TS1, "https://example.com/alpha"),
            req(ID2, TS2, "https://example.com/beta"),
            req(ID3, TS3, "https://example.com/alphabet"),
        )
        val fakeList = FakeRequestsListComponent(items)
        setContent { RequestsListContent(component = fakeList) }

        onAllNodesWithTag(TAG_REQUEST_ITEM).assertCountEquals(COUNT_THREE)
        onNodeWithContentDescription(CD_SEARCH).performClick()
        onNode(hasAnyAncestor(isDialog()) and hasSetTextAction()).performTextInput("alpha")
        onNodeWithText(TEXT_OK).performClick()
        onAllNodesWithTag(TAG_REQUEST_ITEM).assertCountEquals(COUNT_TWO)
        onNodeWithContentDescription(CD_CANCEL_SEARCH).performClick()
        onAllNodesWithTag(TAG_REQUEST_ITEM).assertCountEquals(COUNT_THREE)

        val fakeDetails = FakeRequestDetailsComponent(req(ID10, TS4, "https://example.com/x"))
        setContent { RequestDetailsContent(component = fakeDetails) }
        onNodeWithContentDescription(CD_COPY_ALL).performClick()
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
