package ru.bartwell.kick.module.ktor3.feature.detail.presentation

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test
import ru.bartwell.kick.module.ktor3.core.persist.RequestEntity
import ru.bartwell.kick.module.ktor3.feature.list.data.HttpMethod
import kotlin.test.assertTrue

@Suppress("FunctionNaming")
class RequestDetailsUiTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun copy_action_invoked() {
        val request = RequestEntity(
            id = 1,
            timestamp = 1_000L,
            method = HttpMethod.GET,
            url = "https://example.com/a",
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
        val fake = FakeRequestDetailsComponent(request)
        composeTestRule.setContent { RequestDetailsContent(component = fake) }

        composeTestRule.onNodeWithContentDescription("Copy all details").performClick()
        assertTrue(fake.copyInvoked)
    }
}
