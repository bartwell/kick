package ru.bartwell.kick.runtime.feature.list.presentation

import androidx.compose.runtime.Composable
import ru.bartwell.kick.runtime.core.util.LocalComposeWindow

@Composable
internal actual fun screenCloser(): () -> Unit {
    val window = LocalComposeWindow.current
    return { window?.dispose() }
}
