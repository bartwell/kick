package ru.bartwell.kick.sample.shared.runner

import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import ru.bartwell.kick.module.runner.core.data.PlatformImage
import ru.bartwell.kick.module.runner.core.data.fromImageBitmap
import androidx.core.graphics.createBitmap

internal actual fun createSamplePlatformImage(): PlatformImage? {
    val bmp = createBitmap(96, 96)
    bmp.eraseColor(Color.MAGENTA)
    return PlatformImage.fromImageBitmap(bmp.asImageBitmap())
}
