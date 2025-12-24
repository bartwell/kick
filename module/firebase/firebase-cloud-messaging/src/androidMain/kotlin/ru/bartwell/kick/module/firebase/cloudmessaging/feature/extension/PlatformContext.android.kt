package ru.bartwell.kick.module.firebase.cloudmessaging.feature.extension

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.core.data.get

internal actual fun PlatformContext.copyToClipboard(label: String, text: String) {
    val context: Context = get()
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
    clipboard?.setPrimaryClip(ClipData.newPlainText(label, text))
}
