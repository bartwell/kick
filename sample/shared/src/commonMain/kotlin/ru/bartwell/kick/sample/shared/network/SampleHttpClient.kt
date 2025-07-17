package ru.bartwell.kick.sample.shared.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import ru.bartwell.kick.module.ktor3.KickKtor3Plugin

class SampleHttpClient {

    private val client: HttpClient = HttpClient(getEngineFactory()) {
        install(ContentNegotiation) {
            json()
        }
        install(KickKtor3Plugin)
    }

    suspend fun makeTestRequests() {
        @Suppress("TooGenericExceptionCaught", "PrintStackTrace")
        try {
            client.post("https://jsonplaceholder.typicode.com/posts") {
                contentType(ContentType.Application.Json)
                setBody(mapOf("title" to "foo", "body" to "bar", "userId" to "1"))
            }
            client.get("https://jsonplaceholder.typicode.com/todos/1")
            client.get("https://jsonplaceholder.typicode.com/todos/12345")
            client.get("ftp://jsonplaceholder.typicode.com")
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            client.close()
        }
    }
}

expect fun getEngineFactory(): HttpClientEngineFactory<*>
