package ru.bartwell.kick.module.firebase.cloudmessaging.core.persist.adapter

import app.cash.sqldelight.ColumnAdapter
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

private val json = Json { ignoreUnknownKeys = true }
private val mapSerializer = MapSerializer(String.serializer(), String.serializer())

internal val stringMapAdapter: ColumnAdapter<Map<String, String>, String> =
    object : ColumnAdapter<Map<String, String>, String> {
        override fun decode(databaseValue: String): Map<String, String> =
            runCatching { json.decodeFromString(mapSerializer, databaseValue) }
                .getOrDefault(emptyMap())

        override fun encode(value: Map<String, String>): String =
            json.encodeToString(mapSerializer, value)
    }
