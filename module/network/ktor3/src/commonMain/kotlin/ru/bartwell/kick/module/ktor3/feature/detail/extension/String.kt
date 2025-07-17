package ru.bartwell.kick.module.ktor3.feature.detail.extension

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

@OptIn(ExperimentalSerializationApi::class)
internal fun String.formatJson(): String {
    @Suppress("TooGenericExceptionCaught")
    try {
        val json = Json {
            prettyPrint = true
            prettyPrintIndent = "  "
        }
        val element = json.parseToJsonElement(this)
        return json.encodeToString(JsonElement.serializer(), element)
    } catch (_: Exception) {
        return this
    }
}
