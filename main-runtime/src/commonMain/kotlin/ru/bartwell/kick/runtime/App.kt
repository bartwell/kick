package ru.bartwell.kick.runtime

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ru.bartwell.kick.Kick
import ru.bartwell.kick.core.component.RootComponent
import ru.bartwell.kick.core.data.Theme
import ru.bartwell.kick.runtime.core.component.RootContent

@Composable
internal fun App(rootComponent: RootComponent) {
    MaterialTheme(
        colorScheme = Kick.theme.toColorScheme(),
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .systemBarsPadding()
                .imePadding()
        ) {
            RootContent(
                modifier = Modifier.fillMaxSize(),
                component = rootComponent,
            )
        }
    }
}

@Composable
private fun Theme.toColorScheme(): ColorScheme = when (this) {
    Theme.Auto -> if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()
    Theme.Dark -> darkColorScheme()
    Theme.Light -> lightColorScheme()
    is Theme.Custom -> scheme
}
