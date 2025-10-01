package ru.bartwell.kick.sample.shared.network

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.js.Js

actual fun getEngineFactory(): HttpClientEngineFactory<*> = Js
