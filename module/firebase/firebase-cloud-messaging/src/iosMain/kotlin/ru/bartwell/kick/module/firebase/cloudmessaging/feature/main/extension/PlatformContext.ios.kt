package ru.bartwell.kick.module.firebase.cloudmessaging.feature.main.extension

import platform.UIKit.UIPasteboard
import ru.bartwell.kick.core.data.PlatformContext

internal actual fun PlatformContext.copyToClipboard(label: String, text: String) {
    UIPasteboard.generalPasteboard.apply {
        string = text
    }
}
