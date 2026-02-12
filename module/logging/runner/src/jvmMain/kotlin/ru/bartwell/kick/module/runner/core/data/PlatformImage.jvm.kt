package ru.bartwell.kick.module.runner.core.data

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.toComposeImageBitmap
import java.awt.image.BufferedImage

public actual fun PlatformImage.Companion.fromNative(native: Any?): PlatformImage? = when (native) {
    is PlatformImage -> native
    is BufferedImage -> PlatformImage { BitmapPainter(native.toComposeImageBitmap()) }
    is ImageBitmap -> PlatformImage { BitmapPainter(native) }
    else -> null
}
