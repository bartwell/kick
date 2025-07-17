package ru.bartwell.kick.core.data

import androidx.compose.runtime.Composable

public data object IosPlatformContext : PlatformContext

public fun getPlatformContext(): PlatformContext = IosPlatformContext

@Composable
public actual fun platformContext(): PlatformContext = getPlatformContext()
