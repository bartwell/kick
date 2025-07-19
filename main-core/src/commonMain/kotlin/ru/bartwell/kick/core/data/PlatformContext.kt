package ru.bartwell.kick.core.data

import androidx.compose.runtime.Composable

public interface PlatformContext

@Composable
public expect fun platformContext(): PlatformContext
