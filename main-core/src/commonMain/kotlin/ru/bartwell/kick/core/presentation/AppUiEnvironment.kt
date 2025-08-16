package ru.bartwell.kick.core.presentation

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf

public val LocalAppUiEnvironment: ProvidableCompositionLocal<AppUiEnvironment> = staticCompositionLocalOf {
    error("No AppUiEnvironment value provided")
}

public class AppUiEnvironment(
    public val screenCloser: () -> Unit,
)
