package ru.bartwell.kick.sample.shared.runner

import androidx.compose.ui.graphics.toComposeImageBitmap
import ru.bartwell.kick.module.runner.core.data.PlatformImage
import ru.bartwell.kick.module.runner.core.data.fromImageBitmap
import java.awt.Color
import java.awt.image.BufferedImage

private const val SAMPLE_IMAGE_SIZE = 96
private const val COLOR_RED = 255
private const val COLOR_GREEN = 105
private const val COLOR_BLUE = 180

internal actual fun createSamplePlatformImage(): PlatformImage? {
    val image = BufferedImage(SAMPLE_IMAGE_SIZE, SAMPLE_IMAGE_SIZE, BufferedImage.TYPE_INT_ARGB)
    val graphics = image.createGraphics()
    graphics.color = Color(COLOR_RED, COLOR_GREEN, COLOR_BLUE)
    graphics.fillRect(0, 0, image.width, image.height)
    graphics.dispose()
    return PlatformImage.fromImageBitmap(image.toComposeImageBitmap())
}
