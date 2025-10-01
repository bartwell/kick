package ru.bartwell.kick.module.layout.core.extension

import platform.UIKit.UIPasteboard
import ru.bartwell.kick.core.data.PlatformContext

internal actual fun PlatformContext.copyToClipboard(text: String) {
    UIPasteboard.generalPasteboard.string = text
}
