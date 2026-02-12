package ru.bartwell.kick.module.runner.core.data

import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter

public actual fun PlatformImage.Companion.fromNative(native: Any?): PlatformImage? = when (native) {
    is PlatformImage -> native
    is Bitmap -> PlatformImage { BitmapPainter(native.asImageBitmap()) }
    is ImageBitmap -> PlatformImage { BitmapPainter(native) }
    else -> null
}
