package ru.bartwell.kick.sample.shared.network

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.cio.CIO

actual fun getEngineFactory(): HttpClientEngineFactory<*> = CIO
