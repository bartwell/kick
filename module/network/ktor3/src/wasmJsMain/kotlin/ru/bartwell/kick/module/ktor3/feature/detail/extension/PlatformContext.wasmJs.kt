package ru.bartwell.kick.module.ktor3.feature.detail.extension

import kotlinx.browser.window
import ru.bartwell.kick.core.data.PlatformContext

internal actual fun PlatformContext.copyToClipboard(text: String) {
    val clipboard = window.navigator.clipboard
    clipboard?.writeText(text)
}
