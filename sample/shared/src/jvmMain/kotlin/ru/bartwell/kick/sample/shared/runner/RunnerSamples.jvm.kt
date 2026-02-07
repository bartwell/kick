package ru.bartwell.kick.sample.shared.runner

import java.awt.Color
import java.awt.image.BufferedImage
import androidx.compose.ui.graphics.toComposeImageBitmap
import ru.bartwell.kick.module.runner.core.data.PlatformImage
import ru.bartwell.kick.module.runner.core.data.fromImageBitmap

internal actual fun createSamplePlatformImage(): PlatformImage? {
    val image = BufferedImage(96, 96, BufferedImage.TYPE_INT_ARGB)
    val graphics = image.createGraphics()
    graphics.color = Color(0xFF, 0x69, 0xB4) // hot pink
    graphics.fillRect(0, 0, image.width, image.height)
    graphics.dispose()
    return PlatformImage.fromImageBitmap(image.toComposeImageBitmap())
}
