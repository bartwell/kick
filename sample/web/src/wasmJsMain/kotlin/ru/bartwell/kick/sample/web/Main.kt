package ru.bartwell.kick.sample.web

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document
import ru.bartwell.kick.core.data.getPlatformContext
import ru.bartwell.kick.sample.shared.App
import ru.bartwell.kick.sample.shared.TestDataInitializer

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    TestDataInitializer(getPlatformContext())

    ComposeViewport(document.body!!) {
        App()
    }
}
