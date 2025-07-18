package ru.bartwell.kick.module.ktor3

import io.ktor.client.plugins.api.ClientPlugin
import io.ktor.client.plugins.api.Send
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.content.ByteArrayContent
import io.ktor.content.TextContent
import io.ktor.http.ContentType
import io.ktor.http.contentLength
import io.ktor.http.isSecure
import io.ktor.util.AttributeKey
import io.ktor.utils.io.charsets.Charsets
import io.ktor.utils.io.core.toByteArray
import ru.bartwell.kick.core.persist.RequestEntity
import ru.bartwell.kick.core.util.DateUtils
import ru.bartwell.kick.module.ktor3.core.util.Logger
import ru.bartwell.kick.module.ktor3.feature.list.data.HttpMethod as KickHttpMethod

public val KickKtor3Plugin: ClientPlugin<Config> = createClientPlugin(
    name = "KickKtor3Plugin",
    createConfiguration = ::Config
) {
    val startKey = AttributeKey<Long>("KickStartTime")
    val headersKey = AttributeKey<String>("KickRequestHeaders")
    val bodyKey = AttributeKey<String>("KickRequestBody")
    val config: Config = pluginConfig

    onRequest { request: HttpRequestBuilder, _ ->
        request.attributes.put(startKey, DateUtils.currentTimeMillis())

        val headers = request.headers.entries()
            .joinToString("\n") { "${it.key}: ${it.value.joinToString()}" }
        request.attributes.put(headersKey, headers)
    }

    onResponse { response: HttpResponse ->
        val call = response.call
        val start = call.attributes.getOrNull(startKey) ?: return@onResponse
        val end = DateUtils.currentTimeMillis()
        val duration = end - start

        val request = call.request
        val url = request.url.toString()
        val method = KickHttpMethod.fromString(request.method.value)
        val secure = request.url.protocol.isSecure()

        val respHeaders = response.headers.entries()
            .joinToString("\n") { "${it.key}: ${it.value.joinToString()}" }

        val contentType = response.headers["Content-Type"]
        val responseBody = if (contentType != null &&
            (
                ContentType.parse(contentType).match(ContentType.Text.Any) ||
                    contentType.contains("application/json")
                )
        ) {
            val text = response.bodyAsText(Charsets.UTF_8)
            if (text.toByteArray().size > config.maxBodySizeBytes) {
                text.take(config.maxBodySizeBytes.toInt()) + "...(truncated)"
            } else {
                text
            }
        } else {
            null
        }

        val sizeBytes = response.contentLength() ?: responseBody?.toByteArray()?.size?.toLong()

        val entity = RequestEntity(
            timestamp = start,
            method = method,
            url = url,
            statusCode = response.status.value,
            durationMs = duration,
            responseSizeBytes = sizeBytes,
            isSecure = secure,
            error = null,
            requestHeaders = call.attributes.getOrNull(headersKey),
            requestBody = call.attributes.getOrNull(bodyKey),
            responseHeaders = respHeaders,
            responseBody = responseBody
        )
        Logger.log(entity)
    }

    on(Send) { request ->
        val maybeBody = when (val outgoing = request.body) {
            is ByteArrayContent -> outgoing.bytes().decodeToString()
            is TextContent -> outgoing.text
            else -> null
        }

        maybeBody
            ?.take(config.maxBodySizeBytes.toInt())
            ?.let { request.attributes.put(bodyKey, it) }
        @Suppress("TooGenericExceptionCaught")
        try {
            proceed(request)
        } catch (cause: Throwable) {
            val start = request.attributes.getOrNull(startKey) ?: DateUtils.currentTimeMillis()
            val url = request.url.toString()
            val method = KickHttpMethod.fromString(request.method.value)
            val secure = request.url.protocol.isSecure()
            val hdrs = request.attributes.getOrNull(headersKey)
            val body = request.attributes.getOrNull(bodyKey)
            val duration = DateUtils.currentTimeMillis() - start

            val entity = RequestEntity(
                timestamp = start,
                method = method,
                url = url,
                statusCode = null,
                durationMs = duration,
                responseSizeBytes = null,
                isSecure = secure,
                error = cause.message,
                requestHeaders = hdrs,
                requestBody = body,
                responseHeaders = null,
                responseBody = null
            )
            Logger.log(entity)
            throw cause
        }
    }
}

public class Config {
    @Suppress("MagicNumber")
    public var maxBodySizeBytes: Long = 1024 * 1024
}
