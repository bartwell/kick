package ru.bartwell.kick.sample.shared.runner

import android.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.createBitmap
import ru.bartwell.kick.module.runner.core.data.PlatformImage
import ru.bartwell.kick.module.runner.core.data.fromImageBitmap

private const val SAMPLE_IMAGE_SIZE = 96

internal actual fun createSamplePlatformImage(): PlatformImage? {
    val bmp = createBitmap(SAMPLE_IMAGE_SIZE, SAMPLE_IMAGE_SIZE)
    bmp.eraseColor(Color.MAGENTA)
    return PlatformImage.fromImageBitmap(bmp.asImageBitmap())
}
