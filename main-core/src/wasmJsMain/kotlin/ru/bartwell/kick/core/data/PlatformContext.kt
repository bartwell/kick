package ru.bartwell.kick.core.data

import androidx.compose.runtime.Composable

internal class WasmPlatformContext : PlatformContext

public fun getPlatformContext(): PlatformContext = WasmPlatformContext()

@Composable
public actual fun platformContext(): PlatformContext = WasmPlatformContext()
