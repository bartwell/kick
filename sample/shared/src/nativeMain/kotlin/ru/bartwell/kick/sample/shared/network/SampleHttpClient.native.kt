package ru.bartwell.kick.sample.shared.network

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.darwin.Darwin

actual fun getEngineFactory(): HttpClientEngineFactory<*> = Darwin
