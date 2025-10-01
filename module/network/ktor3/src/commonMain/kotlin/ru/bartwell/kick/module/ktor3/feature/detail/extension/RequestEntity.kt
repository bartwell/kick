package ru.bartwell.kick.module.ktor3.feature.detail.extension

import io.ktor.http.Url
import io.ktor.http.fullPath
import ru.bartwell.kick.core.util.DateUtils
import ru.bartwell.kick.module.ktor3.core.persist.RequestEntity
import ru.bartwell.kick.module.ktor3.feature.list.data.Header

internal fun RequestEntity.buildFullTransactionReport(
    requestHeaders: List<Header>,
    requestBody: String?,
    responseHeaders: List<Header>,
    responseBody: String?,
    error: String?
): String = buildString {
    appendLine("=== Overview ===")
    append("URL: ")
    appendLine(url)
    append("Method: ")
    appendLine(method.name)
    append("Status: ")
    appendLine(statusCode ?: "-")
    append("Request time: ")
    appendLine(formatTimestamp())
    append("Duration: ")
    appendLine(durationMs.formatDuration())
    requestBody?.length?.let { requestSize ->
        append("Request size: ")
        appendLine(requestSize)
    }
    append("Response size: ")
    appendLine(responseSizeBytes?.formatFileSize() ?: "-")
    error?.let {
        append("Error: ")
        appendLine(error)
    }
    appendLine()
    appendLine("=== Request Headers ===")
    if (requestHeaders.isEmpty()) {
        appendLine("[No headers]")
    } else {
        requestHeaders.forEach {
            append(it.key)
            append(": ")
            appendLine(it.value)
        }
    }
    appendLine()
    appendLine("=== Request Body ===")
    appendLine(requestBody?.takeIf { it.isNotBlank() } ?: "[No body]")
    appendLine()
    appendLine("=== Response Headers ===")
    if (responseHeaders.isEmpty()) {
        appendLine("[No headers]")
    } else {
        responseHeaders.forEach {
            append(it.key)
            append(": ")
            appendLine(it.value)
        }
    }
    appendLine()
    appendLine("=== Response Body ===")
    appendLine(responseBody?.takeIf { it.isNotBlank() } ?: "[No body]")
}

internal fun RequestEntity.formatTimestamp(): String {
    return DateUtils.formatLogTime(timestamp)
}

internal fun RequestEntity.formatDuration(): String {
    return durationMs.formatDuration()
}

internal fun String.getDomain(): String {
    return try {
        Url(this).host
    } catch (_: Exception) {
        this
    }
}

internal fun String.getPath(): String {
    return try {
        val url = Url(this)
        val path = url.fullPath
        path.ifEmpty { "/" }
    } catch (_: Exception) {
        this
    }
}

@Suppress("MagicNumber")
internal fun Long.formatDuration(): String {
    return when {
        this < 1000 -> "${this}ms"
        this < 60000 -> "${this / 1000}s"
        else -> "${this / 60000}min"
    }
}

@Suppress("MagicNumber")
internal fun Long.formatFileSize(): String {
    return when {
        this < 1024 -> "${this}B"
        this < 1024 * 1024 -> "${this / 1024}KB"
        this < 1024 * 1024 * 1024 -> "${this / (1024 * 1024)}MB"
        else -> "${this / (1024 * 1024 * 1024)}GB"
    }
}
