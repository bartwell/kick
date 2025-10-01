package ru.bartwell.kick.module.ktor3.feature.detail.presentation

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.module.ktor3.core.persist.RequestEntity
import ru.bartwell.kick.module.ktor3.feature.list.data.Header

internal class FakeRequestDetailsComponent(
    request: RequestEntity,
    reqHeaders: List<Header> = emptyList(),
    reqBody: String? = null,
    respHeaders: List<Header> = emptyList(),
    respBody: String? = null,
) : RequestDetailsComponent {
    private val _model = MutableValue(
        RequestDetailsState(
            request = request,
            requestHeaders = reqHeaders,
            requestBody = reqBody,
            responseHeaders = respHeaders,
            responseBody = respBody,
            selectedTab = 0,
            isLoading = false,
        )
    )
    override val model: Value<RequestDetailsState> get() = _model

    var copyInvoked: Boolean = false
        private set

    override fun onBackPressed() = Unit
    override fun onTabSelected(index: Int) { _model.value = model.value.copy(selectedTab = index) }
    override fun onCopyClick(context: PlatformContext) { copyInvoked = true }
}
