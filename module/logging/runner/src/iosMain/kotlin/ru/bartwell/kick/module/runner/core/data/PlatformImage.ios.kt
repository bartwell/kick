package ru.bartwell.kick.module.runner.core.data

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.toImageBitmap
import platform.UIKit.UIImage

public actual fun PlatformImage.Companion.fromNative(native: Any?): PlatformImage? = when (native) {
    is PlatformImage -> native
    is UIImage -> PlatformImage { BitmapPainter(native.toImageBitmap()) }
    is ImageBitmap -> PlatformImage { BitmapPainter(native) }
    else -> null
}
