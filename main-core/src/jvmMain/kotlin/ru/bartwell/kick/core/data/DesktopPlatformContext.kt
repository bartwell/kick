package ru.bartwell.kick.core.data

import androidx.compose.runtime.Composable

public data object DesktopPlatformContext : PlatformContext

public fun getPlatformContext(): PlatformContext = DesktopPlatformContext

@Composable
public actual fun platformContext(): PlatformContext = getPlatformContext()
