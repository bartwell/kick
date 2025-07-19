package ru.bartwell.kick.module.ktor3.feature.list.data

public enum class HttpMethod {
    GET,
    POST,
    PUT,
    DELETE,
    PATCH,
    HEAD,
    OPTIONS,
    TRACE,
    CONNECT,
    UNKNOWN;

    public companion object {
        public fun fromString(method: String): HttpMethod = entries
            .firstOrNull { it.name == method.uppercase() }
            ?: UNKNOWN
    }
}
