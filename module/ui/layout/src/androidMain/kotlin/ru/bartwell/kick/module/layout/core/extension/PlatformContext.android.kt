package ru.bartwell.kick.module.layout.core.extension

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.core.data.get

internal actual fun PlatformContext.copyToClipboard(text: String) {
    val context = get()
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("Layout", text)
    clipboard.setPrimaryClip(clip)
}
