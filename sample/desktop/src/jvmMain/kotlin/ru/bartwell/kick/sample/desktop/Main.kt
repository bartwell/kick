package ru.bartwell.kick.sample.desktop

import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import ru.bartwell.kick.core.data.getPlatformContext
import ru.bartwell.kick.sample.shared.App
import ru.bartwell.kick.sample.shared.TestDataInitializer

fun main() = application {
    TestDataInitializer(context = getPlatformContext())
    Window(
        title = "Kick Sample",
        resizable = false,
        state = rememberWindowState(
            width = 600.dp,
            height = 600.dp,
            position = WindowPosition.Aligned(Alignment.Center),
        ),
        onCloseRequest = ::exitApplication,
    ) {
        App()
    }
}
