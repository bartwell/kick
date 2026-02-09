package ru.bartwell.kick.module.runner.core.data

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.toComposeImageBitmap
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.usePinned
import org.jetbrains.skia.Image.Companion.makeFromEncoded
import platform.Foundation.NSData
import platform.UIKit.UIImage
import platform.UIKit.UIImagePNGRepresentation
import platform.posix.memcpy

public actual fun PlatformImage.Companion.fromNative(native: Any?): PlatformImage? = when (native) {
    is PlatformImage -> native
    is UIImage -> native.toImageBitmap()?.let { PlatformImage { BitmapPainter(it) } }
    is ImageBitmap -> PlatformImage { BitmapPainter(native) }
    else -> null
}

@OptIn(ExperimentalForeignApi::class)
private fun UIImage.toImageBitmap(): ImageBitmap? {
    val data = UIImagePNGRepresentation(this) ?: return null
    val bytes = data.toByteArray()
    return makeFromEncoded(bytes).toComposeImageBitmap()
}

@OptIn(ExperimentalForeignApi::class)
private fun NSData.toByteArray(): ByteArray {
    val length = length.toInt()
    val byteArray = ByteArray(length)
    byteArray.usePinned { pinned ->
        memcpy(pinned.addressOf(0), bytes, length.convert())
    }
    return byteArray
}
