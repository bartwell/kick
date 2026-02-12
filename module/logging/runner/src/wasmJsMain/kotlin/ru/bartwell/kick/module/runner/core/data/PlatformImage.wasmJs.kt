package ru.bartwell.kick.module.runner.core.data

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter

public actual fun PlatformImage.Companion.fromNative(native: Any?): PlatformImage? = when (native) {
    is PlatformImage -> native
    is ImageBitmap -> PlatformImage { BitmapPainter(native) }
    else -> null
}
