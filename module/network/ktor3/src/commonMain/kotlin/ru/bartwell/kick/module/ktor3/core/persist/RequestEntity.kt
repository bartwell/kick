package ru.bartwell.kick.module.ktor3.core.persist

import ru.bartwell.kick.module.ktor3.feature.list.data.HttpMethod

public data class RequestEntity(
    val id: Long = 0,
    val timestamp: Long,
    val method: HttpMethod,
    val url: String,
    val statusCode: Int?,
    val durationMs: Long,
    val responseSizeBytes: Long?,
    val isSecure: Boolean,
    val error: String?,
    val requestHeaders: String?,
    val requestBody: String?,
    val responseHeaders: String?,
    val responseBody: String?,
)
