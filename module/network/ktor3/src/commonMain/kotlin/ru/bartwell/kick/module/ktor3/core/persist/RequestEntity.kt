package ru.bartwell.kick.core.persist

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.bartwell.kick.module.ktor3.feature.list.data.HttpMethod

@Entity
public data class RequestEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
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
