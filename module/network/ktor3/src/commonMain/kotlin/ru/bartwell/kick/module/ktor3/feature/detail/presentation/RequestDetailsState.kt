package ru.bartwell.kick.module.ktor3.feature.detail.presentation

import ru.bartwell.kick.core.persist.RequestEntity
import ru.bartwell.kick.module.ktor3.feature.list.data.Header

internal data class RequestDetailsState(
    val request: RequestEntity? = null,
    val requestHeaders: List<Header> = emptyList(),
    val requestBody: String? = null,
    val responseHeaders: List<Header> = emptyList(),
    val responseBody: String? = null,
    val selectedTab: Int = 0,
)
