package ru.bartwell.kick.sample.shared.network

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.okhttp.OkHttp

actual fun getEngineFactory(): HttpClientEngineFactory<*> = OkHttp
