package ru.bartwell.kick.module.ktor3.feature.detail.extension

import ru.bartwell.kick.core.data.PlatformContext
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

internal actual fun PlatformContext.copyToClipboard(text: String) {
    val clipboard = Toolkit.getDefaultToolkit().systemClipboard
    val selection = StringSelection(text)
    clipboard.setContents(selection, selection)
}
