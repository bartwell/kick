package ru.bartwell.kick.module.ktor3

import io.ktor.client.plugins.api.ClientPlugin
import io.ktor.client.plugins.api.createClientPlugin

@Suppress("UnusedPrivateProperty", "EmptyFunctionBlock", "unused")
public val KickKtor3Plugin: ClientPlugin<Config> = createClientPlugin(
    name = "KickKtor3Plugin",
    createConfiguration = ::Config
) {}

public class Config {
    @Suppress("MagicNumber")
    public var maxBodySizeBytes: Long = 1024 * 1024
}
