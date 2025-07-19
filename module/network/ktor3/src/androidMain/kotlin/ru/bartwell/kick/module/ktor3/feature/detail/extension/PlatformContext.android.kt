package ru.bartwell.kick.module.ktor3.feature.detail.extension

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.core.data.get

internal actual fun PlatformContext.copyToClipboard(text: String) {
    val context = get()
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("Request Details", text)
    clipboard.setPrimaryClip(clip)
}
